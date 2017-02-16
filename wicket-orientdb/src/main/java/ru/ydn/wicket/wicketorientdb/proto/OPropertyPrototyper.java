package ru.ydn.wicket.wicketorientdb.proto;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * Prototyper for {@link OProperty}
 */
public class OPropertyPrototyper extends AbstractPrototyper<OProperty> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String LINKED_TYPE = "linkedType";
	public static final String LINKED_CLASS = "linkedClass";
	public static final String MANDATORY = "mandatory";
	public static final String READONLY = "readonly";
	public static final String NOT_NULL = "notNull";
	public static final String MIN = "min";
	public static final String MAX = "max";
	public static final String REGEXP = "regexp";
	public static final String COLLATE = "collate";
	public static final String DEFAULT_VALUE = "defaultValue";
	public static final List<String> OPROPERTY_ATTRS = 
			Collections.unmodifiableList(Arrays.asList(NAME, TYPE, LINKED_TYPE, LINKED_CLASS, MANDATORY, READONLY,
							NOT_NULL, MIN, MAX, REGEXP, COLLATE, DEFAULT_VALUE));

	private final String className;
	
	private OPropertyPrototyper(String className)
	{
		this.className = className;
	}

	@Override
	protected OProperty createInstance(OProperty proxy) {
		OSchema schema = OrientDbWebSession.get().getDatabase().getMetadata().getSchema();
		OClass oClass = schema.getClass(className);
		return oClass.createProperty(proxy.getName(), proxy.getType());
	}
	
	@Override
	protected Object handleGet(String propName, Class<?> returnType) {
		if("ownerClass".equals(propName))
		{
			OSchema schema = OrientDbWebSession.get().getDatabase().getMetadata().getSchema();
			return schema.getClass(className);
		}
		else if("fullName".equals(propName))
		{
			return className + "." + values.get(NAME);
		}
		else return super.handleGet(propName, returnType);
	}
	
	@Override
	protected Object handleCustom(Object proxy, Method method, Object[] args) {
		String methodName = method.getName();
		if("compareTo".equals(methodName))
		{
			OProperty otherProperty = (OProperty) args[0];
			String thisName = (String)values.get(NAME);
			return thisName!=null?thisName.compareTo(otherProperty.getName()):1;
		}
		else
		{
			return super.handleCustom(proxy, method, args);
		}
	}

	@Override
	protected Class<OProperty> getMainInterface() {
		return OProperty.class;
	}
	
	public static OProperty newPrototype(String className)
	{
		return newPrototype(className, null);
	}
	
	public static OProperty newPrototype(String className, IPrototypeListener<OProperty> listener)
	{
		return newPrototypeInternal(new OPropertyPrototyper(className), listener);
	}

}
