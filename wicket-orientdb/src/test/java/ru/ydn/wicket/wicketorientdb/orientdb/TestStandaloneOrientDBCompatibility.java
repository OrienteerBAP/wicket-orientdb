package ru.ydn.wicket.wicketorientdb.orientdb;

import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;

import ru.ydn.wicket.wicketorientdb.OrientDbTestWebApplication;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.OPartitionedDatabasePoolFactory;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OStorageException;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

public class TestStandaloneOrientDBCompatibility
{
	
	private OServer startServer() throws Exception
	{
		OServer server = OServerMain.create();
//		server.startup();
		server.startup(OrientDbTestWebApplication.class.getResource("db.config.xml").openStream());
		server.activate();
		return server;
	}
	
	final String PLOCAL_DB_NAME = "plocal:testDBLifeCycle";
	final String MEMORY_DB_NAME = "memory:testDBLifeCycle";
	
	@Test
	public void testPLocalDbLifeCycle() throws Exception
	{
		testOrientDbLifeCycle(PLOCAL_DB_NAME, true, false);
		testOrientDbLifeCycle(PLOCAL_DB_NAME, false, false);
		testOrientDbLifeCycle(PLOCAL_DB_NAME, false, true);
		testOrientDbLifeCycle(PLOCAL_DB_NAME, true, false);
		testOrientDbLifeCycle(PLOCAL_DB_NAME, false, true);
	}
	
	@Test
	@Ignore
	public void testPLocalDbLifeCycleSVT() throws Exception
	{
		for(int i=0;i<5;i++)
			testOrientDbLifeCycle(PLOCAL_DB_NAME, true, true);
	}
	
	@Test
	@Ignore
	public void testMemoryDbLifeCycle() throws Exception
	{
		for(int i=0;i<5;i++)
			testOrientDbLifeCycle(MEMORY_DB_NAME, true, true);
	}
	
	@Test(expected=OStorageException.class)
	@Ignore
	public void testMemoryDBShouldDisapear() throws Exception
	{
		try
		{
			testOrientDbLifeCycle(MEMORY_DB_NAME, true, false);
			testOrientDbLifeCycle(MEMORY_DB_NAME, false, true);
		} finally
		{
			OServer server = OServerMain.server();
			if(server!=null) server.shutdown();
			Orient.instance().shutdown();
		}
	}
	
	public void testOrientDbLifeCycle(String dbURL, boolean createDb, boolean dropDb) throws Exception
	{
		Orient.instance().startup();
		assertNotNull(ODatabaseRecordThreadLocal.INSTANCE);
		Orient.instance().removeShutdownHook();
		OServer server = OServerMain.create();
		server.startup(OrientDbTestWebApplication.class.getResource("db.config.xml").openStream());
		server.activate();
		if(createDb)
		{
			ODatabaseDocument dbToCreate = new ODatabaseDocumentTx(dbURL);
			if(!dbToCreate.exists()) dbToCreate.create();
			dbToCreate.close();
		}
		assertNotNull(ODatabaseRecordThreadLocal.INSTANCE);
		ODatabaseDocument db = new OPartitionedDatabasePoolFactory().get(dbURL, "admin", "admin").acquire();
		db.close();
		assertNotNull(ODatabaseRecordThreadLocal.INSTANCE);
		if(dropDb)
		{
			ODatabaseDocument dbToDrop = new ODatabaseDocumentTx(dbURL);
			dbToDrop.open("admin", "admin");
			dbToDrop.drop();
		}
		server.shutdown();
		Orient.instance().shutdown();
//		Thread.sleep(50);
	}
	
}
