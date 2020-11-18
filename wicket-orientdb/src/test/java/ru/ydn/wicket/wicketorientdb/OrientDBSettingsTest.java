package ru.ydn.wicket.wicketorientdb;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import org.junit.ClassRule;
import org.junit.Test;
import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTesterScope;

import static org.junit.Assert.*;

public class OrientDBSettingsTest {
	@ClassRule
	public static WicketOrientDbTesterScope wicket = new WicketOrientDbTesterScope();
	
	@Test
	public void testInstallingHooks()
	{
		ODatabaseSession db = wicket.getTester().getDatabaseSession();
		OClass clazz = db.getMetadata().getSchema().getClass("TestHooks");
		assertNotNull(clazz);
		try(OResultSet result = db.query("select from TestHooks")) {
			assertTrue(result.hasNext());
			ODocument doc = (ODocument) result.next().getElement().orElse(null);
			assertNotNull(doc);
			assertEquals("HOOK", doc.field("name"));
		}
	}
}
