package ru.ydn.wicket.wicketorientdb.orientdb;

import static org.junit.Assert.assertNotNull;

import com.orientechnologies.orient.core.db.ODatabaseType;
import com.orientechnologies.orient.core.db.OrientDB;
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

public class TestStandaloneOrientDBCompatibility {

	private static final String PLOCAL_DB_NAME = "testDBLifeCycleLocal";
	private static final String MEMORY_DB_NAME = "testDBLifeCycleMemory";
	
	@Test
	public void testPLocalDbLifeCycle() throws Exception {
		testOrientDbLifeCycle(PLOCAL_DB_NAME, ODatabaseType.PLOCAL, true, false);
		testOrientDbLifeCycle(PLOCAL_DB_NAME, ODatabaseType.PLOCAL, false, false);
		testOrientDbLifeCycle(PLOCAL_DB_NAME, ODatabaseType.PLOCAL, false, true);
		testOrientDbLifeCycle(PLOCAL_DB_NAME, ODatabaseType.PLOCAL, true, false);
		testOrientDbLifeCycle(PLOCAL_DB_NAME, ODatabaseType.PLOCAL, false, true);
	}
	
	@Test
	@Ignore
	public void testPLocalDbLifeCycleSVT() throws Exception {
		for (int i = 0; i < 5; i++) {
			testOrientDbLifeCycle(PLOCAL_DB_NAME, ODatabaseType.PLOCAL, true, true);
		}
	}
	
	@Test
	@Ignore
	public void testMemoryDbLifeCycle() throws Exception {
		for (int i = 0; i < 5; i++) {
			testOrientDbLifeCycle(MEMORY_DB_NAME, ODatabaseType.MEMORY, true, true);
		}
	}
	
	@Test(expected=OStorageException.class)
	@Ignore
	public void testMemoryDBShouldDisapear() throws Exception {
		try {
			testOrientDbLifeCycle(MEMORY_DB_NAME, ODatabaseType.MEMORY, true, false);
			testOrientDbLifeCycle(MEMORY_DB_NAME, ODatabaseType.MEMORY, false, true);
		} finally {
			OServer server = OServerMain.server();
			if(server!=null) server.shutdown();
			Orient.instance().shutdown();
		}
	}
	
	public void testOrientDbLifeCycle(String dbName, ODatabaseType type, boolean createDb, boolean dropDb) throws Exception {
		Orient.instance().startup();
		assertNotNull(ODatabaseRecordThreadLocal.instance());
		Orient.instance().removeShutdownHook();
		OServer server = OServerMain.create();
		server.startup(OrientDbTestWebApplication.class.getResource("db.config.xml").openStream());
		server.activate();

		OrientDB orientDB = server.getContext();

		if (createDb && !orientDB.exists(dbName)) {
			orientDB.create(dbName, type);
		}
		assertNotNull(ODatabaseRecordThreadLocal.instance());
		ODatabaseDocument db = orientDB.cachedPool(dbName, "admin", "admin").acquire();
		db.close();
		assertNotNull(ODatabaseRecordThreadLocal.instance());
		if (dropDb) {
			orientDB.drop(dbName);
		}
		server.shutdown();
		Orient.instance().shutdown();
	}

}
