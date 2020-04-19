package ru.ydn.wicket.wicketorientdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.junit.ClassRule;
import org.junit.Test;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTesterScope;
import ru.ydn.wicket.wicketorientdb.model.ListOIndexesModel;
import ru.ydn.wicket.wicketorientdb.model.ListOPropertiesModel;
import ru.ydn.wicket.wicketorientdb.model.OClassModel;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OClassesDataProvider;
import ru.ydn.wicket.wicketorientdb.model.ODocumentLinksDataProvider;
import ru.ydn.wicket.wicketorientdb.model.ODocumentMapWrapper;
import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;
import ru.ydn.wicket.wicketorientdb.model.OIndexesDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OPropertiesDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;
import ru.ydn.wicket.wicketorientdb.model.OPropertyNamingModel;
import ru.ydn.wicket.wicketorientdb.model.OQueryDataProvider;
import ru.ydn.wicket.wicketorientdb.model.OQueryModel;
import ru.ydn.wicket.wicketorientdb.model.SimpleNamingModel;

public class TestModels
{
	@ClassRule
	public static WicketOrientDbTesterScope wicket = new WicketOrientDbTesterScope("admin", "admin");
	
	@Test
	public void testOClassesDataProvider()
	{
		OClassesDataProvider provider = new OClassesDataProvider();
		provider.setSort("name", SortOrder.ASCENDING);
		assertTrue(provider.size()>5);
		Iterator<? extends OClass> it = provider.iterator(0, -1);
		OSchema schema = wicket.getTester().getSchema();
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
		OSchema schema = wicket.getTester().getSchema();
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
		OSchema schema = wicket.getTester().getSchema();
		OClass oClass = schema.getClass("OUser");
		OIndexesDataProvider provider = new OIndexesDataProvider(oClass, true);
		provider.setSort("name", SortOrder.ASCENDING);
		Iterator<? extends OIndex> it = provider.iterator(0, -1);
		List<OIndex> allIndexes = new ArrayList<>(oClass.getIndexes());
		while(it.hasNext())
		{
			OIndex oIndex = it.next();
			assertTrue(allIndexes.remove(provider.model(oIndex).getObject()));
		}
		assertTrue(allIndexes.size()==0);
		provider.detach();
	}
	
	@Test
	public void testListModels()
	{
		IModel<String> classNameModel = Model.of();
		IModel<OClass> classModel = new OClassModel(classNameModel);
		IModel<List<OProperty>> propertiesModel = new ListOPropertiesModel(classModel, null);
		IModel<List<OIndex>> indexesModel = new ListOIndexesModel(classModel, null);
		List<OProperty> properties = propertiesModel.getObject();
		List<OIndex> indexes = indexesModel.getObject();
		assertNotNull(properties);
		assertNotNull(indexes);
		assertTrue(properties.isEmpty());
		assertTrue(indexes.isEmpty());
		classModel.detach();
		propertiesModel.detach();
		indexesModel.detach();
		
		classNameModel.setObject("OUser");
		properties = propertiesModel.getObject();
		indexes = indexesModel.getObject();
		assertNotNull(properties);
		assertNotNull(indexes);
		assertFalse(properties.isEmpty());
		assertFalse(indexes.isEmpty());
	}
	
	@Test
	public void testOQueryProvider()
	{
		OQueryDataProvider<OUser> provider = new OQueryDataProvider<OUser>("select from OUser where name <> :other", OUser.class);
		provider.setSort("name", SortOrder.ASCENDING);
		provider.setParameter("other", Model.of("blalba"));
		Iterator<OUser> it = provider.iterator(0, -1);
		List<ODocument> allUsers = wicket.getTester().getMetadata().getSecurity().getAllUsers();
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
		assertEquals(1, queryModel.size());
		assertEquals("doc1", queryModel.getObject().get(0).field("name"));
		queryModel.detach();
		
		nameModel.setObject("doc2");
		assertEquals(1, queryModel.size());
		assertEquals("doc2", queryModel.getObject().get(0).field("name"));
		queryModel.detach();
		
		nameModel.setObject("doc3");
		assertEquals(1, queryModel.size());
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
		OClass classA = wicket.getTester().getSchema().getClass("ClassA");
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
		OClass oUserClass = wicket.getTester().getSchema().getClass("OUser");
		assertModelObjectEquals(oUserClass, model);
		//Test for null
		model.setObject(null);
		assertModelObjectEquals(null, model);
		//Test for classRename
		OClass newClass = wicket.getTester().getSchema().createClass("TestRenameOClass");
		model.setObject(newClass);
		assertModelObjectEquals(newClass, model);
		newClass.setName("TestRenameOClassRenamed");
		assertModelObjectEquals(newClass, model);
	}
	
	@Test
	public void testODocumentMapWrapper()
	{
		Map<String, Object> map = new ODocumentMapWrapper(new ORecordId("#6:0"));
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
		ODatabaseSession db = wicket.getTester().getDatabaseSession();
		ORecordId recordId = new ORecordId("#6:0");
		ODocumentModel model = new ODocumentModel(recordId);
		assertModelObjectEquals(recordId.getRecord(), model);
		
		recordId = new ORecordId("#6:1");
		model.setObject((ODocument)recordId.getRecord());
		assertModelObjectEquals(recordId.getRecord(), model);
		
		model.setObject(null);
		assertModelObjectEquals(null, model);
		
		//Test persistence between calls
		db.begin();
		ODocument doc = new ODocument("ClassA");
		doc.field("name", "test1");
		model = new ODocumentModel(doc);
		assertEquals("test1", model.getObject().field("name"));
		interupt(db, model);
		assertEquals("test1", model.getObject().field("name"));
		model.getObject().save();
		interupt(db, model);
		//Test that document actually reloaded from DB after closing transaction
		assertEquals("test1", model.getObject().field("name"));
		model.getObject().field("name", "test2");
		assertEquals("test2", model.getObject().field("name"));
		interupt(db, model);
		assertEquals("test1", model.getObject().field("name"));
		//Test autoSave feature
		model.getObject().field("name", "test3");
		model.setAutoSave(true);
		interupt(db, model);
		assertEquals("test3", model.getObject().field("name"));
	}
	
	private void interupt(ODatabaseDocument db, IModel<?> model) {
		model.detach();
		db.commit();
		db.getLocalCache().clear();
		db.begin();
	}
	
	@Test
	public void testODocumentPropertyLocator()
	{
		ORecordId recordId = new ORecordId("#6:0");
		assertModelObjectEquals("admin", new PropertyModel<>(new ODocumentModel(recordId), "name"));
		assertModelObjectEquals("OUser", new PropertyModel<>(new ODocumentModel(recordId), "@schemaClass.name"));
	}
	
	@Test
	public void testOPropertyModel()
	{
		OProperty userNameProperty = wicket.getTester().getSchema().getClass("OUser").getProperty("name");
		OPropertyModel propertyModel = new OPropertyModel("OUser", "name");
		assertModelObjectEquals(userNameProperty, propertyModel);
		//Test for null
		propertyModel.setObject(null);
		assertModelObjectEquals(null, propertyModel);
		//Test for classRename
		OClass newClass = wicket.getTester().getSchema().createClass("TestRenameOProperty");
		OProperty property = newClass.createProperty("newProperty", OType.STRING);
		propertyModel.setObject(property);
		assertModelObjectEquals(property, propertyModel);
		property.setName("newProperty2");
		assertModelObjectEquals(property, propertyModel);
	}
	
	@Test
	public void testODocumentLinksDataProvider()
	{
		ODocument doc1 = new ODocument("ClassA");
		doc1.field("name", "doc1Ext");
		doc1.save();
		ODocument doc2 = new ODocument("ClassA");
		doc2.field("name", "doc2Ext");
		doc2.field("other", Arrays.asList(doc1));
		try {
			ODocumentModel documentModel = new ODocumentModel(doc2);
			OPropertyModel propertyModel = new OPropertyModel("ClassA", "other");
			ODocumentLinksDataProvider provider = new ODocumentLinksDataProvider(documentModel, propertyModel);
			assertEquals(1, provider.size());
			assertEquals(doc1, provider.iterator(0, 1).next());
			doc2.save();
			provider.detach();
			assertEquals(1, provider.size());
			assertEquals(doc1, provider.iterator(0, 1).next());
		} finally {
			doc1.delete();
			doc2.delete();
		}
	}
	
	public static void assertModelObjectEquals(Object expected, IModel<?> model)
	{
		assertEquals(expected, model.getObject());
		model.detach();
		assertEquals(expected, model.getObject());
	}
	
}
