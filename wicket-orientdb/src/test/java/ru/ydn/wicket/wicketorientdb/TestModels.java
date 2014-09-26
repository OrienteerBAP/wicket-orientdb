package ru.ydn.wicket.wicketorientdb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

import ru.ydn.wicket.wicketorientdb.junit.WicketTesterThreadLocal;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OClassesDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OIndexiesDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OPropertiesDataProvider;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

public class TestModels extends AbstractTestClass
{
	@Test
	public void testOClassesDataProvider()
	{
		OClassesDataProvider provider = new OClassesDataProvider();
		provider.setSort("name", SortOrder.ASCENDING);
		assertTrue(provider.size()>5);
		Iterator<? extends OClass> it = provider.iterator(0, -1);
		OSchema schema = getSchema();
		List<OClass> allClasses = new ArrayList<OClass>(schema.getClasses());
		while(it.hasNext())
		{
			OClass oClass = it.next();
			assertTrue(allClasses.remove(oClass));
		}
		assertTrue(allClasses.size()==0);
	}
	
	@Test
	public void testOPropertiesDataProvider()
	{
		OSchema schema = getSchema();
		OClass oClass = schema.getClass("ClassA");
		OPropertiesDataProvider provider = new OPropertiesDataProvider(oClass, true);
		provider.setSort("name", SortOrder.ASCENDING);
		Iterator<? extends OProperty> it = provider.iterator(0, -1);
		List<OProperty> allProperties = new ArrayList<OProperty>(oClass.properties());
		while(it.hasNext())
		{
			OProperty oProperty = it.next();
			assertTrue(allProperties.remove(oProperty));
		}
		assertTrue(allProperties.size()==0);
	}
	
	@Test
	public void testOIndexDataProvider()
	{
		OSchema schema = getSchema();
		OClass oClass = schema.getClass("OUser");
		OIndexiesDataProvider provider = new OIndexiesDataProvider(oClass, true);
		provider.setSort("name", SortOrder.ASCENDING);
		Iterator<? extends OIndex<?>> it = provider.iterator(0, -1);
		List<OIndex<?>> allIndexies = new ArrayList<OIndex<?>>(oClass.getIndexes());
		while(it.hasNext())
		{
			OIndex<?> oIndex = it.next();
			assertTrue(allIndexies.remove(oIndex));
		}
		assertTrue(allIndexies.size()==0);
	}
	
	@Test
	public void testNamingModel()
	{
		IModel<String> keyModel = Model.of("myobject.thisIsMyObject");
		SimpleNamingModel namingModel = new SimpleNamingModel(keyModel);
		assertEquals("This is my object", namingModel.getObject());
		keyModel.setObject("myobject.thatIsMyObject");
		assertEquals("That Is My Object", namingModel.getObject());
	}
	
	@Test
	public void testOClassModel()
	{
		OClassModel model = new OClassModel("OUser");
		OClass oUserClass = getSchema().getClass("OUser");
		assertEquals(oUserClass, model.getObject());
	}
	
}
