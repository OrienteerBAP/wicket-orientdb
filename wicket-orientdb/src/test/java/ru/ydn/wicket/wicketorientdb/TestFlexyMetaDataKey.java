package ru.ydn.wicket.wicketorientdb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.wicket.Application;
import org.junit.ClassRule;
import org.junit.Test;

import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTesterScope;
import ru.ydn.wicket.wicketorientdb.utils.FlexyMetaDataKey;

public class TestFlexyMetaDataKey {

	@ClassRule
	public static WicketOrientDbTesterScope wicket = new WicketOrientDbTesterScope("admin", "admin");
	
	@Test
	public void applicationTest() {
		Application app = wicket.getTester().getApplication();
		assertNull(FlexyMetaDataKey.get(app, "TEST1"));
		assertEquals(app, FlexyMetaDataKey.set(app, "TEST1", "Test1Value"));
		assertEquals("Test1Value", FlexyMetaDataKey.get(app, "TEST1"));
		
		assertTrue(app instanceof OrientDbWebApplication);
		OrientDbWebApplication oApp = (OrientDbWebApplication)app;
		assertEquals(oApp, oApp.setMetaData("TEST2", "Test2Value"));
		assertEquals("Test2Value", oApp.getMetaData("TEST2"));
		assertEquals("Test1Value", oApp.getMetaData("TEST1"));
	}
	
	@Test
	public void sessionTest() {
		OrientDbWebSession session = wicket.getTester().getSession();
		assertNull(FlexyMetaDataKey.get(session, "TEST1"));
		assertEquals(session, FlexyMetaDataKey.set(session, "TEST1", "Test1Value"));
		assertEquals("Test1Value", FlexyMetaDataKey.get(session, "TEST1"));
		
		assertEquals(session, session.setMetaData("TEST2", "Test2Value"));
		assertEquals("Test2Value", session.getMetaData("TEST2"));
		assertEquals("Test1Value", session.getMetaData("TEST1"));
	}
}
