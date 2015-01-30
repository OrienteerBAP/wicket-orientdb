package ru.ydn.wicket.wicketorientdb.orientdb;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTesterScope;
import static org.junit.Assert.*;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.hook.ORecordHook.DISTRIBUTED_EXECUTION_MODE;
import com.orientechnologies.orient.core.hook.ORecordHook.RESULT;
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
	@Ignore
	public void testPropertyRenaming()
	{
		ODatabaseDocument db = wicket.getTester().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		OClass classA = schema.createClass("TestPropertyRenaming");
		OProperty property = classA.createProperty("propertyOld", OType.STRING);
		assertEquals(property, classA.getProperty("propertyOld"));
		assertNull(classA.getProperty("propertyNew"));
		property.setName("propertyNew");
		//schema.reload();
		//classA = schema.getClass("TestPropertyRenaming");
		assertNull(classA.getProperty("propertyOld"));
		assertEquals(property, classA.getProperty("propertyNew"));
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
}
