package ru.ydn.wicket.wicketorientdb.proto;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
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
		oClass.setSuperClasses(proxy.getSuperClasses());
		String clusterSelection = (String) values.get(CLUSTER_SELECTION);
		if(clusterSelection!=null) {
			oClass.setClusterSelection(clusterSelection);
		}

		values.remove(NAME);
		values.remove(SUPER_CLASSES);
		values.remove(CLUSTER_SELECTION);
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
		if(CLUSTER_SELECTION.equals(propName))
		{
			if(value instanceof OClusterSelectionStrategy)
			{
				value = ((OClusterSelectionStrategy)value).getName();
			}
			if(value instanceof CharSequence)
			{
				return super.handleSet(propName, value);
			}
			else return null;
			
		} else if (SUPER_CLASSES.equals(propName)) {
			if(value==null) return super.handleSet(propName, null);
			List<OClass> superClasses = (List<OClass>) value;
			List<String> superClassesNames = new ArrayList<String>(superClasses.size());
			for (OClass oClass : superClasses) {
				superClassesNames.add(oClass.getName());
			}
			return super.handleSet(propName, superClassesNames);
		}
		//Default
		return super.handleSet(propName, value);
	}
	
	@Override
	protected Object handleGet(String propName, Class<?> returnType) {
		if(CLUSTER_SELECTION.equals(propName)) {
			String clusterSelection = (String) values.get(CLUSTER_SELECTION);
			return clusterSelection==null?null:new OClusterSelectionFactory().newInstance(clusterSelection);
		} else if (SUPER_CLASSES.equals(propName)){
			List<OClass> ret = new ArrayList<OClass>();
			List<String> superClassesNames = (List<String>) values.get(SUPER_CLASSES);
			if(superClassesNames!=null && !superClassesNames.isEmpty()) {
				OSchema schema = OrientDbWebSession.get().getDatabase().getMetadata().getSchema();
				for (String superClassName : superClassesNames) {
					OClass superClass = schema.getClass(superClassName);
					if(superClass!=null) ret.add(superClass);
				}
			}
			return ret;
		} else {
			return super.handleGet(propName, returnType);
		}
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
				superClasses = new ArrayList<String>();
				values.put(SUPER_CLASSES, superClasses);
			}

			((List<String>) superClasses).add(((OClass) args[0]).getName());
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
