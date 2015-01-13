package ru.ydn.wicket.wicketorientdb.orientdb;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTesterScope;
import static org.junit.Assert.*;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
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
	@Ignore
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
		doc.removeField("property");
		doc.save();
	}
	
	@Test
	@Ignore
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
}
