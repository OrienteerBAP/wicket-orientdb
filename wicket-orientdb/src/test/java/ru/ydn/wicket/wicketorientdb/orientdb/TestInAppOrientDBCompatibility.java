package ru.ydn.wicket.wicketorientdb.orientdb;

import org.junit.Ignore;
import org.junit.Test;

import ru.ydn.wicket.wicketorientdb.AbstractTestClass;
import static org.junit.Assert.*;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;

public class TestInAppOrientDBCompatibility extends AbstractTestClass
{
	@Test
	@Ignore
	public void testExistsProperty()
	{
		OSchema schema = getSchema();
		OClass classA = schema.createClass("TestExistsA");
		classA.createProperty("property", OType.STRING);
		assertTrue(classA.existsProperty("property"));
		assertNotNull(classA.getProperty("property"));
		OClass classB = schema.createClass("TestExistsB", classA);
		assertNotNull(classB.getProperty("property"));
		assertTrue(classB.existsProperty("property"));
	}
}
