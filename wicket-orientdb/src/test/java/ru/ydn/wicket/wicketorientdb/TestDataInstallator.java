package ru.ydn.wicket.wicketorientdb;

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class TestDataInstallator extends AbstractDataInstallator
{

	@Override
	protected void installData(OrientDbWebApplication app, ODatabaseRecord db) {
		OSchema schema = db.getMetadata().getSchema();
		OClass classA = schema.createClass("ClassA");
		classA.createProperty("name", OType.STRING);
		classA.createProperty("description", OType.STRING);
		OClass classB = schema.createClass("ClassB");
		classB.createProperty("name", OType.STRING);
		classB.createProperty("description", OType.STRING);
		
		new ODocument("ClassA").field("name", "doc1").save();
		new ODocument("ClassA").field("name", "doc2").save();
		new ODocument("ClassA").field("name", "doc3").save();
	}

}