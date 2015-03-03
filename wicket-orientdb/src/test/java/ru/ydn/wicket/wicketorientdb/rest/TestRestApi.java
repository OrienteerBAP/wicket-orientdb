package ru.ydn.wicket.wicketorientdb.rest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.protocol.http.mock.MockHttpServletResponse;
import org.apache.wicket.request.Url;
import org.apache.wicket.util.crypt.Base64;
import org.junit.ClassRule;
import org.junit.ComparisonFailure;
import org.junit.Test;

import ru.ydn.wicket.wicketorientdb.LazyAuthorizationRequestCycleListener;
import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTester;
import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTesterScope;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class TestRestApi
{
	@ClassRule
	public static WicketOrientDbTesterScope wicket = new WicketOrientDbTesterScope();
	
	public static final String TEST_REST_CLASS="TestRest";
	private static final Pattern RID_PATTERN = Pattern.compile("#(\\d+:\\d+)");
	private static final Random RANDOM = new Random();
	
	@Test
	public void testGetDocument() throws Exception
	{
		ODocument doc = (ODocument) wicket.getTester().getDatabase().browseClass(TEST_REST_CLASS).current();
		ORID id = doc.getIdentity();
		String ret = wicket.getTester().executeUrl("orientdb/document/db/"+id.getClusterId()+":"+id.getClusterPosition(), "GET", null);
		assertEquals(doc.toJSON(), ret);
	}
	
	@Test
	public void testPostDocument() throws Exception
	{
		long current = wicket.getTester().getDatabase().countClass(TEST_REST_CLASS);
		String content = "{\"@class\":\"TestRest\",\"a\":\"test2\",\"b\":11,\"c\":false}";
		wicket.getTester().executeUrl("orientdb/document/db/", "POST", content);
		assertEquals(current+1, wicket.getTester().getDatabase().countClass(TEST_REST_CLASS));
	}
	
	@Test
	public void testDeleteDocument() throws Exception
	{
		long current = wicket.getTester().getDatabase().countClass(TEST_REST_CLASS);
		String content = "{\"@class\":\"TestRest\",\"a\":\"todelete\",\"b\":11,\"c\":false}";
		String created = wicket.getTester().executeUrl("orientdb/document/db/", "POST", content);
		assertEquals(current+1, wicket.getTester().getDatabase().countClass(TEST_REST_CLASS));
		Matcher rid = RID_PATTERN.matcher(created);
		assertTrue(rid.find());
		wicket.getTester().executeUrl("orientdb/document/db/"+rid.group(1), "DELETE", content);
		assertEquals(current, wicket.getTester().getDatabase().countClass(TEST_REST_CLASS));
	}
	
	@Test
	public void testQueryAndUpdate() throws Exception
	{
		ODocument doc = (ODocument) wicket.getTester().getDatabase().browseClass(TEST_REST_CLASS).current();
		String ret = wicket.getTester().executeUrl("orientdb/query/db/sql/select+from+"+TEST_REST_CLASS, "GET", null);
		assertTrue(ret.contains(doc.toJSON()));
		
		int nextB = RANDOM.nextInt();
		ret = wicket.getTester().executeUrl("orientdb/command/db/sql", "POST", "update "+TEST_REST_CLASS+" set b = "+nextB);
		doc.reload();
		assertEquals(nextB, doc.field("b"));
	}
	
	@Test
	public void testQueryCoding() throws Exception
	{
		OSecurityUser currentUser = wicket.getTester().getDatabase().getUser();
		ODocument userDoc = currentUser.getDocument();
		String rid = userDoc.getIdentity().toString();
		String sql = "select * from OUser where @rid = "+rid;
		String url = "orientdb/query/db/sql/"+URLEncoder.encode(sql, "UTF8");
		String ret = wicket.getTester().executeUrl(url, "GET", null);
		assertTrue(ret.contains(userDoc.toJSON()));
	}
	
	@Test
	public void testExecuteFunction() throws Exception
	{
		String ret = wicket.getTester().executeUrl("orientdb/function/db/fun1", "GET", null);
		assertTrue(ret.contains("fun1"));
		ret = wicket.getTester().executeUrl("orientdb/function/db/fun2", "POST", null);
		assertTrue(ret.contains("fun2"));
	}
	
	@Test(expected=IOException.class)
	public void testExecuteFailing() throws Exception
	{
		String ret = wicket.getTester().executeUrl("orientdb/function/db/fun2", "GET", null);
		System.out.println("ret="+ret);
	}
	
	@Test
	public void testAuthentication() throws Exception
	{
		WicketOrientDbTester tester = wicket.getTester();
		assertFalse(tester.isSignedIn());
		assertNull(tester.getSession().getUser());
		assertContains(tester.getDatabase().getUser().getDocument().toJSON(), getCurrentUser());
		tester.signIn("writer", "writer");
		assertTrue(tester.isSignedIn());
		assertEquals("writer", tester.getSession().getUser().getName());
		assertContains(tester.getSession().getUser().getDocument().toJSON(), getCurrentUser());
		tester.signOut();
		assertFalse(tester.isSignedIn());
		assertContains(tester.getDatabase().getUser().getDocument().toJSON(), getCurrentUser());
		tester.signIn("admin", "admin");
		assertTrue(tester.isSignedIn());
		assertEquals("admin", tester.getSession().getUser().getName());
		assertContains(tester.getSession().getUser().getDocument().toJSON(),getCurrentUser());
		
		tester.signOut();
		assertFalse(tester.isSignedIn());
		assertContains(tester.getDatabase().getUser().getDocument().toJSON(), getCurrentUser());
		
		String currentUser = getCurrentUser("admin", "admin");
		assertTrue(tester.isSignedIn());
		assertEquals("admin", tester.getSession().getUser().getName());
		assertContains(tester.getSession().getUser().getDocument().toJSON(), currentUser);
		
	}
	
	private static void assertContains(String where, String what)
	{
		if(what!=null && !what.contains(where))
		{
			throw new ComparisonFailure("Expected containing.", where, what);
		}
	}
	
	private String getCurrentUser() throws Exception
	{
		return getCurrentUser(null, null);
	}
	
	private String getCurrentUser(String username, String password) throws Exception
	{
		return wicket.getTester().executeUrl("orientdb/query/db/sql/select+from+$user", "GET", null, username, password);
	}
	
}
