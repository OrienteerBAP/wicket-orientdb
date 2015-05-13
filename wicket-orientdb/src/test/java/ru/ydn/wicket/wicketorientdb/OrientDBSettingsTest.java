package ru.ydn.wicket.wicketorientdb;

import java.util.List;

import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.*;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTesterScope;

public class OrientDBSettingsTest {
	@ClassRule
	public static WicketOrientDbTesterScope wicket = new WicketOrientDbTesterScope();
	
	@Test
	public void testInstallingHooks()
	{
		ODatabaseDocument db = wicket.getTester().getDatabase();
		OClass clazz = db.getMetadata().getSchema().getClass("TestHooks");
		assertNotNull(clazz);
		List<ODocument> ret = db.query(new OSQLSynchQuery<ODocument>("select from TestHooks"));
		assertTrue(ret.size()>0);
		ODocument doc = ret.get(0);
		assertEquals("HOOK", doc.field("name"));
	}
}
