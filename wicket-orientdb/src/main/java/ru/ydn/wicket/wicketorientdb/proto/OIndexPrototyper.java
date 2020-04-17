package ru.ydn.wicket.wicketorientdb.proto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.util.lang.Args;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.index.OIndexDefinition;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Prototyper for {@link OIndex}
 */
public class OIndexPrototyper extends AbstractPrototyper<OIndex>
{
	private static final long serialVersionUID = 1L;

	/**
	 * Inner interface to make prototype name and type writable 
	 */
	public static interface IMakeSomeFieldsWritableFix
	{
		public void setName(String name);
		public void setType(String type);
		public void setMetadata(ODocument doc);
		public void setAlgorithm(String algorithm);
	}
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String DEF = "definition";
	public static final String DEF_CLASS_NAME = "definition.className";
	public static final String DEF_FIELDS = "definition.fields";
	public static final String DEF_FILED_TO_INDEX = "definition.fieldsToIndex";
	public static final String DEF_COLLATE = "definition.collate";
	public static final String DEF_NULLS_IGNORED = "definition.nullValuesIgnored";
	public static final String SIZE = "size";
	public static final String KEY_SIZE = "keySize";
	public static final String ALGORITHM = "algorithm";
	public static final String METADATA = "metadata";

	public static final List<String> OINDEX_ATTRS = 
				Collections.unmodifiableList(Arrays.asList(NAME, TYPE, ALGORITHM, DEF_CLASS_NAME, 
									DEF_FIELDS, DEF_COLLATE, DEF_NULLS_IGNORED, METADATA, SIZE, KEY_SIZE));
	public static final List<String> RW_ATTRS = 
				Collections.unmodifiableList(Arrays.asList(DEF_COLLATE, DEF_NULLS_IGNORED));

	private static final Class<?>[] FIX_INTERFACES = new Class<?>[]{IMakeSomeFieldsWritableFix.class}; 
	
	
	public OIndexPrototyper(String className, List<String> fields)
	{
		Args.notEmpty(fields, "fields");
		values.put(DEF_CLASS_NAME, className);
		values.put(DEF_FIELDS, fields);
		values.put(DEF_NULLS_IGNORED, true);
		if(fields!=null && fields.size()==1) {
			values.put(NAME, className+"."+fields.get(0));
		}
	}
	
	@Override
	protected OIndex createInstance(OIndex proxy) {
		OSchema schema = OrientDbWebSession.get().getDatabase().getMetadata().getSchema();
		OClass oClass = schema.getClass(proxy.getDefinition().getClassName());
		String name = proxy.getName();
		List<String> fields = proxy.getDefinition().getFields();
		String type = proxy.getType();
		if(name==null) name=oClass.getName()+"."+fields.get(0);
		ODocument metadata = proxy.getMetadata();
		String algorithm = proxy.getAlgorithm();
		values.keySet().retainAll(RW_ATTRS);
		return oClass.createIndex(name, type, null, metadata, algorithm, fields.toArray(new String[0]));
	}
	
	@Override
	protected Object getDefaultValue(String propName, Class<?> returnType) {
		if(DEF.equals(propName))
		{
			return prototypeForChild(propName, OIndexDefinition.class);
		}
		else
		{
			return super.getDefaultValue(propName, returnType);
		}
	}

	@Override
	protected Class<OIndex> getMainInterface() {
		return OIndex.class;
	}
	
	public static OIndex newPrototype(String className, List<String> fields)
	{
		return newPrototype(className, fields, null);
	}

	public static OIndex newPrototype(String className, List<String> fields, IPrototypeListener<OIndex> listener)
	{
		return newPrototypeInternal(new OIndexPrototyper(className, fields), listener);
	}

	@Override
	protected Class<?>[] getAdditionalInterfaces() {
		return FIX_INTERFACES;
	}
	
	
	

}
