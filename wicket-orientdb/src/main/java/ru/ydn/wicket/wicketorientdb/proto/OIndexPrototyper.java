package ru.ydn.wicket.wicketorientdb.proto;

import java.util.Arrays;
import java.util.List;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper.OClassSetNameFix;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter.MapSplitter;
import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.index.OIndexDefinition;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

@SuppressWarnings("rawtypes")
public class OIndexPrototyper extends AbstractPrototyper<OIndex>
{
	public static interface MakeNameAndTypeWritableFix
	{
		public void setName(String name);
		public void setType(String type);
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

	public static final List<String> OINDEX_ATTRS = Arrays.asList(NAME, TYPE, DEF_CLASS_NAME, DEF_FIELDS, DEF_FILED_TO_INDEX, DEF_COLLATE, DEF_NULLS_IGNORED, SIZE, KEY_SIZE);
	public static final List<String> RW_ATTRS = Arrays.asList(DEF_COLLATE, DEF_NULLS_IGNORED);

	private static final Class<?>[] FIX_INTERFACES = new Class<?>[]{MakeNameAndTypeWritableFix.class}; 
	
	private final String className;
	private final String[] fields;
	
	public OIndexPrototyper(String className, String[] fields)
	{
		this.className = className;
		this.fields = fields;
	}
	
	@Override
	protected OIndex<?> createInstance(OIndex proxy) {
		OSchema schema = OrientDbWebSession.get().getDatabase().getMetadata().getSchema();
		OClass oClass = schema.getClass(className);
		String name = proxy.getName();
		String type = proxy.getType();
		values.keySet().retainAll(RW_ATTRS);
		return oClass.createIndex(name, type, fields);
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

	public static OIndex<?> newPrototype(String className, String[] fields)
	{
		return newPrototype(new OIndexPrototyper(className, fields));
	}

	@Override
	protected Class<?>[] getAdditionalInterfaces() {
		return FIX_INTERFACES;
	}
	
	
	

}
