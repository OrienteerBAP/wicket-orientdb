package ru.ydn.wicket.wicketorientdb.utils.proto;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

public class OClassPrototyper extends AbstractPrototyper<OClass> {

	@Override
	protected OClass createInstance() {
		OSchema schema = OrientDbWebSession.get().getDatabase().getMetadata().getSchema();
		schema.createClass(values.get("name").toString());
		return schema.createClass(values.get("name").toString());
	}

	@Override
	protected Class<OClass> getMainInterface() {
		return OClass.class;
	}
	
	public static OClass newPrototype()
	{
		return newPrototype(new OClassPrototyper());
	}

}
