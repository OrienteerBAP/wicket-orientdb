package ru.ydn.wicket.wicketorientdb.orientdb;

import org.junit.Test;

import ru.ydn.wicket.wicketorientdb.OrientDbTestWebApplication;
import static org.junit.Assert.*;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

public class TestStandaloneOrientDBCompatibility
{
	
	@Test
	public void testSubSequentMemoryDBOpen() throws Exception
	{
		OServer server = startServer();
		
		
		final String DB_NAME = "memory:testDBOpen";
		
		ODatabaseDocument db = new ODatabaseDocumentTx(DB_NAME);
		db.create();
		db.close();
		
		Orient.instance().registerThreadDatabaseFactory(new ODatabaseThreadLocalFactory() {
			private ODatabaseDocumentPool pool = ODatabaseDocumentPool.global();
			
			@Override
			public ODatabaseDocumentInternal getThreadDatabase() {
				return pool.acquire(DB_NAME, "admin", "admin");
			}
		});
		db = ODatabaseRecordThreadLocal.INSTANCE.get();
		assertFalse(db.isClosed());
		assertNotNull(db.getMetadata());
		db.close();
		assertTrue(db.isClosed());
		ODatabaseRecordThreadLocal.INSTANCE.remove();
		Orient.instance().shutdown();
		server.shutdown();
		
		server = startServer();
		Orient.instance().startup();
		
		db = new ODatabaseDocumentTx(DB_NAME);
		db.create();
		db.close();
		
		db = ODatabaseRecordThreadLocal.INSTANCE.get();
		assertFalse(db.isClosed());
		assertNotNull(db.getMetadata());
		db.close();
		assertTrue(db.isClosed());
	}
	
	private OServer startServer() throws Exception
	{
		OServer server = OServerMain.create();
//		server.startup();
		server.startup(OrientDbTestWebApplication.class.getResource("db.config.xml").openStream());
		server.activate();
		return server;
	}
	
}
