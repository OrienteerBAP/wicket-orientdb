package ru.ydn.wicket.wicketorientdb;

import com.orientechnologies.orient.core.db.*;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestODatabasePoolFactory {

    @Test
    @Ignore
    // run this test separate from wicket-orientdb
    public void testEvictions() {
        OrientDB orientDB = new OrientDB("embedded:testdb", OrientDBConfig.defaultConfig());
        orientDB.createIfNotExists("testdb", ODatabaseType.MEMORY);

        ODatabasePoolFactory factory = new ODatabasePoolFactory(orientDB, 2);

        ODatabasePool poolAdmin1 = factory.get("testdb", "admin", "admin");
        ODatabasePool poolAdmin2 = factory.get("testdb", "admin", "admin");
        ODatabasePool poolReader1 = factory.get("testdb", "reader", "reader");
        ODatabasePool poolReader2 = factory.get("testdb", "reader", "reader");

        assertEquals(poolAdmin1, poolAdmin2);
        assertEquals(poolReader1, poolReader2);
        assertNotEquals(poolAdmin1, poolReader1);

        ODatabasePool poolWriter1 = factory.get("testdb", "writer", "writer");
        ODatabasePool poolWriter2 = factory.get("testdb", "writer", "writer");
        assertEquals(poolWriter1, poolWriter2);

        ODatabasePool poolAdmin3 = factory.get("testdb", "admin", "admin");
        assertNotEquals(poolAdmin1, poolAdmin3);
    }
}
