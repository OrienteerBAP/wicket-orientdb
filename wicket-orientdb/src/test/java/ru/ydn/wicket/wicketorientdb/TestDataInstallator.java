package ru.ydn.wicket.wicketorientdb;

import java.util.Arrays;

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
		classA.createProperty("other", OType.LINKLIST).setLinkedClass(classA);
		OClass classB = schema.createClass("ClassB");
		classB.createProperty("name", OType.STRING);
		classB.createProperty("description", OType.STRING);
		
		ODocument doc1 = new ODocument("ClassA").field("name", "doc1");
		ODocument doc2 = new ODocument("ClassA").field("name", "doc2");
		ODocument doc3 = new ODocument("ClassA").field("name", "doc3");
		doc1.field("other", Arrays.asList(doc2, doc3));
		doc2.field("other", Arrays.asList(doc1, doc3));
		doc3.field("other", Arrays.asList(doc1, doc2));
		
		doc1.save();
		doc2.save();
		doc3.save();
	}

}
