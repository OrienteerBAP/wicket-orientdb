package ru.ydn.wicket.wicketorientdb.orientdb;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.junit.ClassRule;
import org.junit.ComparisonFailure;
import org.junit.Ignore;
import org.junit.Test;

import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTester;
import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTesterScope;
import static org.junit.Assert.*;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.hook.ORecordHook.DISTRIBUTED_EXECUTION_MODE;
import com.orientechnologies.orient.core.hook.ORecordHook.RESULT;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.function.OFunction;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class TestInAppOrientDBCompatibility
{
	@ClassRule
	public static WicketOrientDbTesterScope wicket = new WicketOrientDbTesterScope();
	
	@Test
	public void testGettingFields()
	{
		OSchema schema = wicket.getTester().getSchema();
		OClass classA = schema.createClass("GettingFields");
		classA.createProperty("title", OType.STRING);
		classA.createProperty("link", OType.LINK).setLinkedClass(classA);
		classA.createProperty("multi", OType.LINKLIST).setLinkedClass(classA);
		ODocument doc = new ODocument(classA);
		doc.field("title", "test");
		doc.save();
		doc.field("link", doc);
		doc.field("multi", Arrays.asList(doc));
		doc.save();
		doc.reload();
		String title = doc.field("title");
		ODocument link = doc.field("link");
		Collection<ODocument> multi = doc.field("multi");
	}
	
	@Test
	public void testExistsProperty()
	{
		OSchema schema = wicket.getTester().getSchema();
		OClass classA = schema.createClass("TestExistsA");
		classA.createProperty("property", OType.STRING);
		assertTrue(classA.existsProperty("property"));
		assertNotNull(classA.getProperty("property"));
		OClass classB = schema.createClass("TestExistsB", classA);
		assertNotNull(classB.getProperty("property"));
		assertTrue(classB.existsProperty("property"));
	}
	
	@Test
	public void testRemovingReadonlyField()
	{
		ODatabaseDocument db = wicket.getTester().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		OClass classA = schema.createClass("TestRemovingField");
		classA.createProperty("name", OType.STRING);
		OProperty property = classA.createProperty("property", OType.STRING);
		property.setReadonly(true);
		
		ODocument doc = new ODocument(classA);
		doc.field("name", "My Name");
		doc.field("property", "value1");
		doc.save();
		
		doc.field("name", "My Name 2");
		doc.field("property", "value2");
		doc.undo("property");
		doc.save();
	}
	
	@Test
	public void testRemovingReadonlyField2()
	{
		ODatabaseDocument db = wicket.getTester().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		OClass classA = schema.createClass("TestRemovingField2");
		classA.createProperty("name", OType.STRING);
		OProperty property = classA.createProperty("property", OType.STRING);
		property.setReadonly(true);
		
		ODocument doc = new ODocument(classA);
		doc.field("name", "My Name");
		doc.field("property", "value1");
		doc.save();
		
		doc.field("name", "My Name 2");
		doc.field("property", "value2");
		doc.undo();
		doc.field("name", "My Name 3");
		doc.save();
	}
	
	@Test
	public void testPropertyRenaming()
	{
		ODatabaseDocument db = wicket.getTester().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		OClass classA = schema.createClass("TestPropertyRenaming");
		OProperty property = classA.createProperty("propertyOld", OType.STRING);
		assertEquals(property, classA.getProperty("propertyOld"));
		assertNull(classA.getProperty("propertyNew"));
		property.setName("propertyNew");
		schema.reload();
		classA = schema.getClass("TestPropertyRenaming");
		assertNull(classA.getProperty("propertyOld"));
		assertEquals(property, classA.getProperty("propertyNew"));
	}
	
	private static final Random RANDOM = new Random();
	
	@Test
	public void testOFunctions() throws Exception
	{
		ODatabaseDocument db = wicket.getTester().getDatabase();
		ODocument doc =  new ODocument(OFunction.CLASS_NAME);
		doc.field("name", "testResurection");
		doc.field("language", "JavaScript");
		doc.field("idempotent", true);
		doc.save();
		ORID orid = doc.getIdentity();
		for(int i=0;i<10;i++)
		{
			db = wicket.getTester().getDatabase();
			String signature = "signature"+RANDOM.nextLong();
			boolean isGoodCall = (i+1)%3 != 0;
			db.begin();
			doc = orid.getRecord();
			String code = isGoodCall?"return \""+signature+"\";":"return nosuchvar;";
			doc.field("code", code);
			doc.save();
			db.commit();
			db.close();
			if(isGoodCall)
			{
				String result;
				for(int j=0; j<3;j++)
				{
					result = wicket.getTester().executeUrl("orientdb/function/db/testResurection", "GET", null);
					assertContains(signature, result);
				}
			}
			else
			{
				try
				{
					wicket.getTester().executeUrl("orientdb/function/db/testResurection", "GET", null);
					assertFalse("We should be there, because function should have 400 response", true);
				} catch (Exception e)
				{
					//NOP
				}
			}
		}
	}
	
	private static void assertContains(String where, String what)
	{
		if(what!=null && !what.contains(where))
		{
			throw new ComparisonFailure("Expected containing.", where, what);
		}
	}
	
	private static final String TEST_VALIDATION_AND_HOOKS_CLASS= "TestValidationAndHooks";
	@Test
	@Ignore
	public void testValidationAndHooksOrder()
	{
		ODatabaseDocument db = wicket.getTester().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		OClass classA = schema.createClass(TEST_VALIDATION_AND_HOOKS_CLASS);
		classA.createProperty("property1", OType.STRING).setNotNull(true);
		classA.createProperty("property2", OType.STRING).setReadonly(true);
		classA.createProperty("property3", OType.STRING).setMandatory(true);
		db.registerHook(new ODocumentHookAbstract() {
			
			
			
			@Override
			public RESULT onRecordBeforeCreate(ODocument doc) {
				doc.field("property1", "value1-create");
				doc.field("property2", "value2-create");
				doc.field("property3", "value3-create");
				return RESULT.RECORD_CHANGED;
			}

			@Override
			public RESULT onRecordBeforeUpdate(ODocument doc) {
				doc.undo("property2");
				if(doc.field("property3")==null) doc.field("property3", "value3-update");
				return RESULT.RECORD_CHANGED;
			}

			@Override
			public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
				return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
			}
		});
		ODocument doc = new ODocument(classA);
		//doc.field("property3", "value3-create"); //Magic line #1
		doc.save();
		assertEquals("value1-create", doc.field("property1"));
		assertEquals("value2-create", doc.field("property2"));
		assertEquals("value3-create", doc.field("property3"));
		
		doc.field("property1", "value1-update");
		doc.field("property2", "value2-update");
		doc.removeField("property3");
		//doc.undo("property2"); //Magic line #2
		//doc.field("property3", "value3-update"); //Magic line #3
		doc.save();
		assertEquals("value1-update", doc.field("property1"));
		assertEquals("value2-create", doc.field("property2"));
		assertEquals("value3-update", doc.field("property3"));
	}
	
	@Test
	@Ignore
	public void testUpdatesInHooks()
	{
		final String className = "TestUpdatesInHook";
		ODatabaseDocument db = wicket.getTester().getDatabase();
		db.registerHook(new ODocumentHookAbstract() {
			
			{
				setIncludeClasses(className);
			}
			
			@Override
			public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
				return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
			}
			
			@Override
			public RESULT onRecordBeforeCreate(ODocument iDocument) {
				return onRecordBeforeUpdate(iDocument);
			}

			@Override
			public RESULT onRecordBeforeUpdate(ODocument iDocument) {
				iDocument.undo("b");
				return RESULT.RECORD_CHANGED;
			}
			
			@Override
			public void onRecordAfterCreate(ODocument iDocument) {
				onRecordAfterUpdate(iDocument);
			}
			
			@Override
			public void onRecordAfterUpdate(ODocument iDocument) {
				onRecordAfterRead(iDocument);
			}
			
			@Override
			public void onRecordAfterRead(ODocument iDocument) {
				if(!iDocument.containsField("b"))
					iDocument.field("b", iDocument.field("a"));
			}
			
			
		});
		OSchema schema = db.getMetadata().getSchema();
		OClass classA = schema.createClass(className);
		classA.createProperty("a", OType.STRING);
		classA.createProperty("b", OType.STRING);
		db.commit();
		ODocument doc = new ODocument(classA);
		/*doc.field("a", "test1");
		doc.save();
		doc.reload();
		assertEquals(doc.field("a"), doc.field("b"));
		doc.field("a", "test2");
		doc.save();
		doc.reload();
		assertEquals(doc.field("a"), doc.field("b"));*/
		db.begin();
		doc.field("a", "test3");
		doc.save();
		doc.reload();
//		doc = db.reload(doc, null, true);
		assertEquals(doc.field("a"), doc.field("b"));
		doc.field("a", "test4");
		doc.save();
		doc.reload();
//		doc = db.reload(doc, null, true);
		assertEquals(doc.field("a"), doc.field("b"));
		db.commit();
	}
	
	@Test
	public void testAuthentication() throws Exception
	{
		WicketOrientDbTester tester = wicket.getTester();
		String content = tester.executeUrl("orientdb/query/db/sql/select+from+$user", "GET", null, "reader", "reader");
		System.out.println(content);
		assertTrue(content.contains("reader"));
		content = tester.executeUrl("orientdb/query/db/sql/select+from+$user", "GET", null, "writer",  "writer");
		System.out.println(content);
		assertTrue(content.contains("writer"));
		content = tester.executeUrl("orientdb/query/db/sql/select+from+$user", "GET", null, "admin",  "admin");
		System.out.println(content);
		assertTrue(content.contains("admin"));
	}
}
