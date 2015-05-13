package ru.ydn.wicket.wicketorientdb;

import java.util.Arrays;

import ru.ydn.wicket.wicketorientdb.rest.TestRestApi;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class TestDataInstallator extends AbstractDataInstallator
{

	@Override
	protected void installData(OrientDbWebApplication app, ODatabaseDocument db) {
		OSchema schema = db.getMetadata().getSchema();
		OClass classA = schema.createClass("ClassA");
		classA.createProperty("name", OType.STRING);
		classA.createProperty("description", OType.STRING);
		classA.createProperty("other", OType.LINKLIST).setLinkedClass(classA);
		classA.createProperty("empty", OType.LINKLIST).setLinkedClass(classA);
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
		
		OClass testRest = schema.createClass(TestRestApi.TEST_REST_CLASS);
		testRest.createProperty("a", OType.STRING);
		testRest.createProperty("b", OType.INTEGER);
		testRest.createProperty("c", OType.BOOLEAN);
		ODocument restDoc = new ODocument(testRest);
		restDoc.field("a", "test");
		restDoc.field("b", 10);
		restDoc.field("c", true);
		restDoc.save();
		
		OClass function = schema.getClass("OFunction");
		ODocument fun1 = new ODocument(function);
		fun1.field("name", "fun1");
		fun1.field("language", "javascript");
		fun1.field("idempotent", true);
		fun1.field("code", "return \"fun1\";");
		fun1.save();
		
		ODocument fun2 = new ODocument(function);
		fun2.field("name", "fun2");
		fun2.field("language", "javascript");
		fun2.field("idempotent", false);
		fun2.field("code", "return \"fun2\";");
		fun2.save();
		
		OClass classTestHooks = schema.createClass("TestHooks");
		classTestHooks.createProperty("name", OType.STRING);
		ODocument testHook = new ODocument(classTestHooks);
		testHook.field("name", "SAVED");
		testHook.save();
	}

}
