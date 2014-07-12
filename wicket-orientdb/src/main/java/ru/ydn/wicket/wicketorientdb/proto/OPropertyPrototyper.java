package ru.ydn.wicket.wicketorientdb.proto;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

public class OPropertyPrototyper extends AbstractPrototyper<OProperty> {
	
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String LINKED_TYPE = "linkedType";
	public static final String LINKED_CLASS = "linkedClass";
	public static final String MANDATORY = "mandatory";
	public static final String READONLY = "readonly";
	public static final String NOT_NULL = "notNull";
	public static final String MIN = "min";
	public static final String MAX = "max";
	public static final String COLLATE = "collate";
	public static String[] OPROPERTY_ATTRS = new String[]{NAME, TYPE, LINKED_TYPE, LINKED_CLASS, MANDATORY, READONLY, NOT_NULL, MIN, MAX, COLLATE};

	
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
	protected Class<OProperty> getMainInterface() {
		return OProperty.class;
	}
	
	public static OProperty newPrototype(String className)
	{
		return newPrototype(new OPropertyPrototyper(className));
	}

}
