package ru.ydn.wicket.wicketorientdb.orientdb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
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
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.hook.ORecordHook.DISTRIBUTED_EXECUTION_MODE;
import com.orientechnologies.orient.core.hook.ORecordHook.RESULT;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.function.OFunction;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.OClusterSelectionStrategy;
import com.orientechnologies.orient.core.metadata.security.OSecurityShared;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

public class TestInAppOrientDBCompatibility
{
	@ClassRule
	public static WicketOrientDbTesterScope wicket = new WicketOrientDbTesterScope("admin", "admin");
	
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
	
	@Test
	@Ignore
	public void testSerialization() throws Exception
	{
		ODatabaseDocument db = wicket.getTester().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		OClass classA = schema.createClass("SerA");
		OClass classB = schema.createClass("SerB");
		
		classA.createProperty("name", OType.STRING);
		classB.createProperty("name", OType.STRING);
		classA.createProperty("child", OType.LINKLIST, classB);
		classB.createProperty("parent", OType.LINK, classA);
		
		ODocument docA = new ODocument(classA);
		docA.field("name", "Doc A");
		doSerialization(docA);
		assertEquals(0, db.countClass(docA.getClassName()));
		ODocument docB = new ODocument(classB);
		docB.field("name", "Doc B");
		doSerialization(docB);
		assertEquals(0, db.countClass(docB.getClassName()));
		docA.field("child", Arrays.asList(docB));
		doSerialization(docA);
		assertEquals(0, db.countClass(docA.getClassName()));
		assertEquals(0, db.countClass(docB.getClassName()));
		doSerialization(docB);
		assertEquals(0, db.countClass(docA.getClassName()));
		assertEquals(0, db.countClass(docB.getClassName()));
		docB.field("parent", Arrays.asList(docA));
		doSerialization(docA);
		assertEquals(0, db.countClass(docA.getClassName()));
		assertEquals(0, db.countClass(docB.getClassName()));
		doSerialization(docB);
		assertEquals(0, db.countClass(docA.getClassName()));
		assertEquals(0, db.countClass(docB.getClassName()));
	}
	
	private void doSerialization(ODocument doc) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(doc);
		oos.flush();
		oos.close();
	}
	
	@Test
	public void testTransactions() throws Exception {
		ODatabaseDocument db = wicket.getTester().getDatabase();
		try {
			assertFalse(db.getTransaction().isActive());
			OSchema schema = db.getMetadata().getSchema();
			OClass classA = schema.createClass("TransA");
			classA.createProperty("name", OType.STRING);
			ODocument doc = new ODocument(classA);
			doc.field("name", "test1");
			doc.save();
			ORID orid = doc.getIdentity();
			db.begin();
			assertTrue(db.getTransaction().isActive());
			doc = orid.getRecord();
			assertEquals("test1", doc.field("name"));
			doc.field("name", "test2");
			doc = orid.getRecord();
			assertEquals("test2", doc.field("name"));
			//There is NO SAVE!
			db.commit();
			db.getLocalCache().clear();
			/* COMMENT START */
			//db.close();
			//db = wicket.getTester().getDatabase();
			/* COMMENT STOP */
			doc = orid.getRecord();
			assertEquals("test1", doc.field("name"));
			
		} finally {
			db.commit();
		}
	}
	
	
	@Test
	@Ignore
	public void testTransactions2() throws Exception {
		ODatabaseDocument db = wicket.getTester().getDatabase();
		try {
			assertFalse(db.getTransaction().isActive());
			OSchema schema = db.getMetadata().getSchema();
			OClass classA = schema.createClass("TransB");
			classA.createProperty("name", OType.STRING);
			ODocument doc = new ODocument(classA);
			doc.field("name", "test1");
			doc.save();
			ORID orid = doc.getIdentity();
			
			db.begin();
			assertTrue(db.getTransaction().isActive());
			doc = orid.getRecord();
			assertEquals("test1", doc.field("name"));
			doc.field("name", "test2");
			doc.save();
			doc = orid.getRecord();
			assertEquals("test2", doc.field("name"));
			doc.field("name", "test3");
			assertEquals("test3", doc.field("name"));
			//There is NO SAVE!
			db.commit();
			db.getLocalCache().clear();
			/* COMMENT START */
			//db.close();
			//db = wicket.getTester().getDatabase();
			/* COMMENT STOP */
			doc = orid.getRecord();
			assertEquals("test2", doc.field("name"));
			
		} finally {
			db.commit();
		}
	}
	
	@Test
	public void testInHook() throws Exception {
		ODatabaseDocument db = wicket.getTester().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		OClass oClass = schema.createClass("TestInHook");
		oClass.createProperty("a", OType.INTEGER);
		oClass.createProperty("b", OType.INTEGER);
		oClass.createProperty("c", OType.INTEGER);
		ODocument doc = new ODocument(oClass);
		doc.field("a", 2);
		doc.field("b", 2);
		doc.save();
		doc.reload();
		assertEquals(2, (Object) doc.field("a"));
		assertEquals(2, (Object) doc.field("b"));
		assertNull(doc.field("c"));
		db.registerHook(new ODocumentHookAbstract(db) {
			
			{
				setIncludeClasses("TestInHook");
			}
			
			@Override
			public void onRecordAfterCreate(ODocument iDocument) {
				onRecordAfterRead(iDocument);
			}
			
			@Override
			public void onRecordAfterRead(ODocument iDocument) {
				String script = "select sum(a, b) as value from "+iDocument.getIdentity();
				List<ODocument> calculated = database.query(new OSQLSynchQuery<Object>(script));
				if(calculated!=null && !calculated.isEmpty())
				{
					iDocument.field("c", (Object) calculated.get(0).field("value"));
				}
			}
			
			@Override
			public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
				return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
			}
		});
		doc.reload();
		assertEquals(2, (Object) doc.field("a"));
		assertEquals(2, (Object) doc.field("b"));
		assertEquals(4, (Object) doc.field("c"));
		
		doc = new ODocument(oClass);
		doc.field("a", 3);
		doc.field("b", 3);
		doc.save();
		
		assertEquals(3, (Object) doc.field("a"));
		assertEquals(3, (Object) doc.field("b"));
		assertEquals(6, (Object) doc.field("c"));
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
					iDocument.field("b", (Object) iDocument.field("a"));
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
	public void testDocumentTrackingSimple()
	{
		final String className = "TestDocumentTrackingSimple";
		ODatabaseDocument db = wicket.getTester().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		final OClass classA = schema.createClass(className);
		classA.createProperty("a", OType.STRING);
		db.registerHook(new ORecordHook() {

			@Override
			public void onUnregister() {
				// NOP
			}

			@Override
			public RESULT onTrigger(TYPE iType, ORecord iRecord) {
				if(iRecord instanceof ODocument) {
					ODocument doc = (ODocument) iRecord;
					if(classA.isSuperClassOf(doc.getSchemaClass())) {
//						System.out.println("During "+iType+" document should track changes: "+doc.isTrackingChanges());
						assertTrue("During "+iType+" document should track changes", doc.isTrackingChanges());
					}
				}
				return RESULT.RECORD_NOT_CHANGED;
			}

			@Override
			public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
				return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
			}
						
		});
		db.commit();
		ODocument doc = new ODocument(classA);
		doc.field("a", "test");
		doc.save();
		doc.reload();
		doc.field("a", "test2");
		doc.save();
		doc.reload();
		doc.delete();
	}
	
	@Test
	@Ignore
	public void testDocumentTrackingComplex()
	{
		final String className = "TestDocumentTrackingComplex";
		ODatabaseDocument db = wicket.getTester().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		final OClass classA = schema.createClass(className);
		classA.createProperty("a", OType.STRING);
		classA.createProperty("b", OType.STRING);
		db.registerHook(new ODocumentHookAbstract() {
			
			{
				setIncludeClasses(className);
			}
			
			@Override
			public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
				return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
			}
			
			@Override
			public void onRecordAfterCreate(ODocument iDocument) {
				System.out.println("onRecordAfterCreate");
				iDocument.field("a", "onRecordAfterCreate");
			}
			
			@Override
			public void onRecordAfterUpdate(ODocument iDocument) {
				System.out.println("onRecordAfterUpdate");
				iDocument.field("a", "onRecordAfterUpdate");
			}
			
			@Override
			public RESULT onRecordBeforeUpdate(ODocument iDocument) {
				System.out.println("onRecordAfterUpdate before undo: "+iDocument.field("a"));
				iDocument.undo("a");
				System.out.println("onRecordAfterUpdate after  undo: "+iDocument.field("a"));
				return RESULT.RECORD_CHANGED;
			}
			
			@Override
			public void onRecordAfterRead(ODocument iDocument) {
				assertEquals("original", iDocument.field("a"));
			}
		});
		db.commit();
		ODocument doc = new ODocument(classA);
		doc.field("a", "original");
		doc.save();
		doc.field("b", "other change");
		doc.save();
		doc.reload();
		doc.field("a", "updated");
		doc.save();
		doc.field("b", "other change");
		doc.save();
		doc.reload();
		doc.delete();
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
	
	@Test
	@Ignore
	public void testLoosingLinkedClass() throws Exception
	{
		ODatabaseDocument db = wicket.getTester().getDatabase();
		OSchema schema = wicket.getTester().getSchema();
		OClass mainClass = schema.createClass("LMainClass");
		OClass embeddedClass = schema.createClass("LEmbeddedClass");
		mainClass.createProperty("name", OType.STRING);
		mainClass.createProperty("embedded", OType.EMBEDDED).setLinkedClass(embeddedClass);
		embeddedClass.createProperty("name", OType.STRING);
		
		db.begin();
		ODocument main = new ODocument(mainClass);
		main.field("name", "main");
		ODocument embedded = new ODocument(embeddedClass);
		//embedded.field("name", "embedded");
		main.field("embedded", embedded);
		//NO Save here!
		db.commit();
		db.close();
		
		main.fromStream(main.toStream());
		
		db = wicket.getTester().getDatabase();
		db.begin();
		assertEmbeddedIsCorrect(main);
		main.save();
		ORID recordId = main.getIdentity();
		db.commit();
		db.close();
		
		db = wicket.getTester().getDatabase();
		db.begin();
		main = recordId.getRecord();
		assertEmbeddedIsCorrect(main);
		db.commit();
	}
	
	@Test
	@Ignore
	public void testDeletionOfDependentClass()
	{
		ODatabaseDocument db = wicket.getTester().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		OClass oRestricted = schema.getClass(OSecurityShared.RESTRICTED_CLASSNAME);
		OClass classA = schema.createClass("TestDeletionOfDependentClassA", oRestricted);
		OClass classB = schema.createClass("TestDeletionOfDependentClassB", classA);
		schema.dropClass(classB.getName());
	}
	
	@Test
	@Ignore
	public void testClassChange()
	{
		ODatabaseDocument db = wicket.getTester().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		OClass classA = schema.createClass("TestClassChangeA");
		OClass classB = schema.createClass("TestClassChangeB");
		ODocument doc = new ODocument(classA);
		doc.save();
		doc = doc.getIdentity().getRecord();
		doc.setClassName(classB.getName());
		assertEquals(classB.getName(), doc.getClassName());
		doc = doc.getIdentity().getRecord();
		assertEquals(classB.getName(), doc.getClassName());
		ORID id = doc.getIdentity();
		db.commit(true);
		db.close();
		db = wicket.getTester().getDatabase();
		doc = id.getRecord();
		assertEquals(classB.getName(), doc.getClassName());
	}
	
	@Test
	public void testDeletionOfDependentClass2()
	{
		ODatabaseDocument db = wicket.getTester().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		OClass classAbs = schema.createAbstractClass("TestDeletionAbst");
		OClass classA = schema.createClass("TestDeletionA", classAbs);
		ODocument doc = new ODocument(classA);
		doc.save();
		assertEquals(1, classA.count());
		OClass classB = schema.createClass("TestDeletionB", classA);
		doc = new ODocument(classB);
		doc.save();
		assertEquals(1, classB.count());
		assertEquals(2, classA.count());
		schema.dropClass(classB.getName());
		assertEquals(1, classA.count());
	}
	
	@Test
	public void testCreationInHook()
	{
		ODatabaseDocument db = wicket.getTester().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		final OClass classA = schema.createClass("TestCreationInHookMain");
		final OClass classB = schema.createClass("TestCreationInHookReflect");
		classA.createProperty("name", OType.STRING);
		classA.createProperty("mirror", OType.LINK).setLinkedClass(classB);
		classB.createProperty("name", OType.STRING);
		db.registerHook(new ODocumentHookAbstract() {
			
			{
				setIncludeClasses(classA.getName());
			}
			
			@Override
			public RESULT onRecordBeforeCreate(ODocument iDocument) {
				ODocument mirror = new ODocument(classB);
				mirror.field("name", (Object) iDocument.field("name"));
				iDocument.field("mirror", mirror);
				return RESULT.RECORD_CHANGED;
			}
			
			@Override
			public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
				return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
			}
		});
		
		for(int i=0;i<10;i++) {
			String name = "name-"+RANDOM.nextLong();
			ODocument doc = new ODocument(classA);
			doc.field("name", name);
			doc.save();
		}
		db.commit();
		db.close();
		db = wicket.getTester().getDatabase();
		
		for(ODocument doc : db.browseClass(classA.getName())) {
			String name = doc.field("name");
			assertNotNull(name);
			ODocument mirror = doc.field("mirror");
			assertNotNull(mirror);
			assertEquals(name, mirror.field("name"));
		}
	}
	
	private void assertEmbeddedIsCorrect(ODocument doc) throws Exception
	{
		assertEquals("main", doc.field("name"));
		ODocument embedded = doc.field("embedded");
		assertNotNull(embedded);
//		assertEquals("embedded", embedded.field("name"));
		assertNotNull(getImmutableClass(embedded));
		assertNotNull(embedded.getSchemaClass());
	}
	
	private static OClass getImmutableClass(ODocument doc) throws Exception
	{
		Method method = ODocument.class.getDeclaredMethod("getImmutableSchemaClass");
		method.setAccessible(true);
		return (OClass) method.invoke(doc);
	}
	
	@Ignore //TODO: Uncomment when OrientDB issue will be fixed: https://github.com/orientechnologies/orientdb/issues/8067
	@Test
	public void testLinkToOUser() {
		ODatabaseDocument db = wicket.getTester().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		final OClass classA = schema.createClass("TestLinkToOUser");
		classA.createProperty("name", OType.STRING);
		classA.createProperty("user", OType.LINK).setLinkedClass(schema.getClass("OUser"));
		ORID userRid = new ORecordId("#5:0");
		ODocument doc = new ODocument(classA);
		wicket.getTester().signIn("writer", "writer");
		db = wicket.getTester().getDatabase();
		db.begin();
		ODocument userDoc = userRid.getRecord();
		userDoc.field("roles");
		doc.field("Admin");
		doc.field("user", userDoc);
		doc.save();
		db.commit();
	}
}
