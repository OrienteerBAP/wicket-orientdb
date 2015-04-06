/**
 * Copyright (C) 2015 Ilia Naryzhny (phantom@ydn.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.ydn.wicket.wicketorientdb.orientdb;

import org.junit.Ignore;
import org.junit.Test;

import ru.ydn.wicket.wicketorientdb.OrientDbTestWebApplication;
import static org.junit.Assert.*;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;
import com.orientechnologies.orient.core.db.OPartitionedDatabasePoolFactory;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OStorageException;
import com.orientechnologies.orient.core.hook.ODocumentHookAbstract;
import com.orientechnologies.orient.core.hook.OHookThreadLocal;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.hook.ORecordHook.DISTRIBUTED_EXECUTION_MODE;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;

public class TestStandaloneOrientDBCompatibility {
    public static final String PLOCAL_DB_NAME = "plocal:testDBLifeCycle";
    public static final String MEMORY_DB_NAME = "memory:testDBLifeCycle";

    @Test
    public void testSubSequentMemoryDBOpen() throws Exception {
        OServer server = startServer();

        final String dbName = "memory:testDBOpen";

        ODatabaseDocument db = new ODatabaseDocumentTx(dbName);
        db.create();
        db.close();

        Orient.instance().registerThreadDatabaseFactory(new ODatabaseThreadLocalFactory() {
            private ODatabaseDocumentPool pool = ODatabaseDocumentPool.global();

            @Override
            public ODatabaseDocumentInternal getThreadDatabase() {
                return pool.acquire(dbName, "admin", "admin");
            }
        });
        db = ODatabaseRecordThreadLocal.INSTANCE.get();
        assertFalse(db.isClosed());
        assertNotNull(db.getMetadata());
        db.close();
        assertTrue(db.isClosed());
        ODatabaseRecordThreadLocal.INSTANCE.remove();
        server.shutdown();
        Orient.instance().shutdown();

        server = startServer();
        Orient.instance().startup();

        db = new ODatabaseDocumentTx(dbName);
        db.create();
        db.close();

        db = ODatabaseRecordThreadLocal.INSTANCE.get();
        assertFalse(db.isClosed());
        assertNotNull(db.getMetadata());
        db.close();
        assertTrue(db.isClosed());
        server.shutdown();
        Orient.instance().shutdown();
    }

    private OServer startServer() throws Exception {
        OServer server = OServerMain.create();
//        server.startup();
        server.startup(OrientDbTestWebApplication.class.getResource("db.config.xml").openStream());
        server.activate();
        return server;
    }

    @Test
    public void testPLocalDbLifeCycle() throws Exception {
        testOrientDbLifeCycle(PLOCAL_DB_NAME, true, false);
        testOrientDbLifeCycle(PLOCAL_DB_NAME, false, false);
        testOrientDbLifeCycle(PLOCAL_DB_NAME, false, true);
        testOrientDbLifeCycle(PLOCAL_DB_NAME, true, false);
        testOrientDbLifeCycle(PLOCAL_DB_NAME, false, true);
    }

    @Test
    @Ignore
    public void testPLocalDbLifeCycleSVT() throws Exception {
        for (int i = 0; i < 5; i++) {
            testOrientDbLifeCycle(PLOCAL_DB_NAME, true, true);
        }
    }

    @Test
    @Ignore
    public void testMemoryDbLifeCycle() throws Exception {
        for (int i = 0; i < 5; i++) {
            testOrientDbLifeCycle(MEMORY_DB_NAME, true, true);
        }
    }

    @Test(expected = OStorageException.class)
    @Ignore
    public void testMemoryDBShouldDisapear() throws Exception {
        try {
            testOrientDbLifeCycle(MEMORY_DB_NAME, true, false);
            testOrientDbLifeCycle(MEMORY_DB_NAME, false, true);
        } finally {
            OServer server = OServerMain.server();
            if (server != null) {
                server.shutdown();
            }
            Orient.instance().shutdown();
        }
    }

    public void testOrientDbLifeCycle(String dbURL, boolean createDb, boolean dropDb) throws Exception {
        Orient.instance().startup();
        assertNotNull(ODatabaseRecordThreadLocal.INSTANCE);
        assertNotNull(OHookThreadLocal.INSTANCE);
        Orient.instance().removeShutdownHook();
        OServer server = OServerMain.create();
        server.startup(OrientDbTestWebApplication.class.getResource("db.config.xml").openStream());
        server.activate();
        if (createDb) {
            ODatabaseDocument dbToCreate = new ODatabaseDocumentTx(dbURL);
            if (!dbToCreate.exists()) {
                dbToCreate.create();
            }
            dbToCreate.close();
        }
        assertNotNull(ODatabaseRecordThreadLocal.INSTANCE);
        assertNotNull(OHookThreadLocal.INSTANCE);
        ODatabaseDocument db = new OPartitionedDatabasePoolFactory().get(dbURL, "admin", "admin").acquire();
        db.close();
        assertNotNull(ODatabaseRecordThreadLocal.INSTANCE);
        assertNotNull(OHookThreadLocal.INSTANCE);
        if (dropDb) {
            ODatabaseDocument dbToDrop = new ODatabaseDocumentTx(dbURL);
            dbToDrop.open("admin", "admin");
            dbToDrop.drop();
        }
        server.shutdown();
        Orient.instance().shutdown();
//        Thread.sleep(50);
    }

}
