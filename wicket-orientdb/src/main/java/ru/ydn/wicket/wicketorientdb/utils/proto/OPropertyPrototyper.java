package ru.ydn.wicket.wicketorientdb.utils.proto;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

public class OPropertyPrototyper extends AbstractPrototyper<OProperty> {
	
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
