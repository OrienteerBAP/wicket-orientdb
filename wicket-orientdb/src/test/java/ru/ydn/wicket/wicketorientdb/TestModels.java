package ru.ydn.wicket.wicketorientdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.junit.Test;

import static org.junit.Assert.*;

import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.ListOIndexiesModel;
import ru.ydn.wicket.wicketorientdb.model.ListOPropertiesModel;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OClassesDataProvider;
import ru.ydn.wicket.wicketorientdb.model.ODocumentMapWrapper;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OIndexiesDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OPropertiesDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
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
			assertTrue(allClasses.remove(provider.model(oClass).getObject()));
		}
		assertTrue(allClasses.size()==0);
		provider.detach();
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
			assertTrue(allProperties.remove(provider.model(oProperty).getObject()));
		}
		assertTrue(allProperties.size()==0);
		provider.detach();
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
			assertTrue(allIndexies.remove(provider.model(oIndex).getObject()));
		}
		assertTrue(allIndexies.size()==0);
		provider.detach();
	}
	
	@Test
	public void testListModels()
	{
		IModel<String> classNameModel = Model.of();
		IModel<OClass> classModel = new OClassModel(classNameModel);
		IModel<List<OProperty>> propertiesModel = new ListOPropertiesModel(classModel, null);
		IModel<List<OIndex<?>>> indexiesModel = new ListOIndexiesModel(classModel, null);
		List<OProperty> properties = propertiesModel.getObject();
		List<OIndex<?>> indexies = indexiesModel.getObject();
		assertNotNull(properties);
		assertNotNull(indexies);
		assertTrue(properties.isEmpty());
		assertTrue(indexies.isEmpty());
		classModel.detach();
		propertiesModel.detach();
		indexiesModel.detach();
		
		classNameModel.setObject("OUser");
		properties = propertiesModel.getObject();
		indexies = indexiesModel.getObject();
		assertNotNull(properties);
		assertNotNull(indexies);
		assertFalse(properties.isEmpty());
		assertFalse(indexies.isEmpty());
	}
	
	@Test
	public void testOQueryProvider()
	{
		OQueryDataProvider<OUser> provider = new OQueryDataProvider<OUser>("select from OUser where name <> :other", OUser.class);
		provider.setSort("name", SortOrder.ASCENDING);
		provider.setParameter("other", Model.of("blalba"));
		Iterator<OUser> it = provider.iterator(0, -1);
		List<ODocument> allUsers = getMetadata().getSecurity().getAllUsers();
		assertTrue(provider.size()==allUsers.size());
		while(it.hasNext())
		{
			OUser oUser = it.next();
			assertTrue(allUsers.contains(provider.model(oUser).getObject().getDocument()));
		}
		provider.detach();
		assertTrue(provider.size()==allUsers.size());
	}
	
	@Test
	public void testOQueryModelSimple()
	{
		IModel<String> nameModel = Model.of();
		OQueryModel<ODocument> queryModel = new OQueryModel<ODocument>("select from ClassA where name = :name");
		queryModel.setParameter("name", nameModel);
		nameModel.setObject("doc1");
		assertEquals("doc1", queryModel.getObject().get(0).field("name"));
		queryModel.detach();
		
		nameModel.setObject("doc2");
		assertEquals("doc2", queryModel.getObject().get(0).field("name"));
		queryModel.detach();
		
		nameModel.setObject("doc3");
		assertEquals("doc3", queryModel.getObject().get(0).field("name"));
		queryModel.detach();
	}
	
	@Test
	public void testOQueryModelExpandNotEmpty()
	{
		IModel<String> nameModel = Model.of();
		OQueryModel<ODocument> queryModel = new OQueryModel<ODocument>("select expand(other) from ClassA where name = :name");
		queryModel.setParameter("name", nameModel);
		nameModel.setObject("doc1");
		assertEquals(2, queryModel.size());
		
		queryModel = new OQueryModel<ODocument>("select expand(other) from ClassA");
		assertEquals(6, queryModel.size());
	}
	
	@Test
	public void testOQueryModelExpandEmpty()
	{
		IModel<String> nameModel = Model.of();
		OQueryModel<ODocument> queryModel = new OQueryModel<ODocument>("select expand(empty) from ClassA where name = :name");
		queryModel.setParameter("name", nameModel);
		nameModel.setObject("doc1");
		assertEquals(0, queryModel.size());
		
		queryModel = new OQueryModel<ODocument>("select expand(empty) from ClassA");
		assertEquals(0, queryModel.size());
	}
	
	@Test
	public void testOQueryModelDBTouch()
	{
		OClass classA = getSchema().getClass("ClassA");
		ODocument doc = new ODocument(classA);
		doc.field("other", Arrays.asList(doc));
		OQueryModel<ODocument> queryModel = new OQueryModel<ODocument>("select  from ClassA where :doc in other");
		queryModel.setParameter("doc", new ODocumentModel(doc));
		long was = classA.count();
		assertTrue(doc.getIdentity().isNew());
		assertEquals(0, queryModel.size());
		assertTrue(doc.getIdentity().isNew());
		assertEquals(was, classA.count());
	}
	
	@Test
	public void testNamingModel()
	{
		IModel<String> keyModel = Model.of("myobject.thisIsMyObject");
		SimpleNamingModel<String> namingModel = new SimpleNamingModel<String>(keyModel);
		assertModelObjectEquals("This is my object", namingModel);
		keyModel.setObject("myobject.thatIsMyObject");
		assertModelObjectEquals("That Is My Object", namingModel);
		namingModel.detach();
		assertModelObjectEquals("That Is My Object", namingModel);
	}
	
	@Test
	public void testOClassNamingModel()
	{
		IModel<String> classNameModel = Model.of("OUser");
		IModel<OClass> oClassModel = new OClassModel(classNameModel);
		OClassNamingModel model = new OClassNamingModel(oClassModel);
		assertModelObjectEquals("OUser", model);
		model.detach();
		classNameModel.setObject("ORole");
		assertModelObjectEquals("SuperRole", model);
	}
	
	@Test
	public void testOPropertyNamingModel()
	{
		IModel<String> classNameModel = Model.of("OUser");
		IModel<OClass> oClassModel = new OClassModel(classNameModel);
		IModel<OProperty> propertyModel = new OPropertyModel(oClassModel, "name");
		OPropertyNamingModel model = new OPropertyNamingModel(propertyModel);
		assertModelObjectEquals("Name", model);
		model.detach();
		classNameModel.setObject("ORole");
		assertModelObjectEquals("Role Name", model);
	}
	
	@Test
	public void testOClassModel()
	{
		OClassModel model = new OClassModel("OUser");
		OClass oUserClass = getSchema().getClass("OUser");
		assertModelObjectEquals(oUserClass, model);
	}
	
	@Test
	public void testODocumentMapWrapper()
	{
		Map<String, Object> map = new ODocumentMapWrapper(new ORecordId("#5:0"));
		assertTrue(map.containsKey("name"));
		assertEquals("admin", map.get("name"));
		assertTrue(map.size()>0);
		assertTrue(map.keySet().contains("name"));
		assertTrue(map.values().contains("admin"));
		assertTrue(map.entrySet().size()==map.size());
	}
	
	@Test
	public void testODocumentModel()
	{
		ORecordId recordId = new ORecordId("#5:0");
		ODocumentModel model = new ODocumentModel(recordId);
		assertModelObjectEquals(recordId.getRecord(), model);
		
		recordId = new ORecordId("#5:1");
		model.setObject((ODocument)recordId.getRecord());
		assertModelObjectEquals(recordId.getRecord(), model);
		
		model.setObject(null);
		assertModelObjectEquals(null, model);
	}
	
	@Test
	public void testODocumentPropertyModel()
	{
		ORecordId recordId = new ORecordId("#5:0");
		ODocumentPropertyModel<String> model = new ODocumentPropertyModel<String>(new ODocumentModel(recordId), "name");
		assertModelObjectEquals("admin", model);
	}
	
	@Test
	public void testOPropertyModel()
	{
		OProperty userNameProperty = getSchema().getClass("OUser").getProperty("name");
		OPropertyModel propertyModel = new OPropertyModel("OUser", "name");
		assertModelObjectEquals(userNameProperty, propertyModel);
	}
	
	public static void assertModelObjectEquals(Object expected, IModel<?> model)
	{
		assertEquals(expected, model.getObject());
		model.detach();
		assertEquals(expected, model.getObject());
	}
	
}
