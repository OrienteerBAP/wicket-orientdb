package ru.ydn.wicket.wicketorientdb.proto;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.OClusterSelectionFactory;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.OClusterSelectionStrategy;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Prototyper for {@link OClass}
 */
public class OClassPrototyper extends AbstractPrototyper<OClass> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "name";
	public static final String SHORT_NAME = "shortName";
	public static final String SUPER_CLASSES = "superClasses";
	public static final String OVER_SIZE = "overSize";
	public static final String STRICT_MODE = "strictMode";
	public static final String ABSTRACT = "abstract";
	public static final String CLUSTER_SELECTION = "clusterSelection";
	public static final String JAVA_CLASS = "javaClass";
	
	public static final List<String> OCLASS_ATTRS = Arrays.asList(NAME, SHORT_NAME, SUPER_CLASSES, OVER_SIZE, STRICT_MODE, ABSTRACT, JAVA_CLASS, CLUSTER_SELECTION);
	

	private OClassPrototyper()
	{
		values.put("overSize", (float)0);
	}

	@Override
	protected OClass createInstance(OClass proxy) {
		OSchema schema = OrientDbWebSession.get().getDatabase().getMetadata().getSchema();
		OClass oClass = schema.createClass(proxy.getName());
		oClass.setSuperClasses((List<? extends OClass>) values.get(SUPER_CLASSES));
		values.remove("name");
		return oClass;
	}

	@Override
	protected Class<OClass> getMainInterface() {
		return OClass.class;
	}
	
	public static OClass newPrototype()
	{
		return newPrototype(new OClassPrototyper());
	}
	
	@Override
	protected Object handleSet(String propName, Object value) {
		if("clusterSelection".equals(propName))
		{
			if(value instanceof CharSequence)
			{
				value = new OClusterSelectionFactory().newInstance(value.toString());
			}
			if(value instanceof OClusterSelectionStrategy)
			{
				return super.handleSet(propName, value);
			}
			else return null;
			
		}
		//Default
		return super.handleSet(propName, value);
	}

	@Override
	protected Object handleCustom(Object proxy, Method method, Object[] args) {
		String methodName = method.getName();
		if("properties".equals(methodName) || "declaredProperties".equals(methodName))
		{
			return Collections.EMPTY_SET;
		}
		else if("addSuperClass".equals(methodName))
		{
			Object superClasses = values.get(SUPER_CLASSES);
			if (superClasses == null)
			{
				superClasses = new ArrayList<OClass>();
				values.put(SUPER_CLASSES, superClasses);
			}

			((List<OClass>) superClasses).add((OClass) args[0]);
			return proxy;
		}
		else
		{
			return super.handleCustom(proxy, method, args);
		}
	}

	@Override
	public String toString() {
		return "Prototype for '"+getMainInterface().getName()+"'";
	}

}
