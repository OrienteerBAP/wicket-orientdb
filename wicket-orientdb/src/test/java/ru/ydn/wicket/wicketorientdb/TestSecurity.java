package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;

import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTester;
import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTesterScope;
import ru.ydn.wicket.wicketorientdb.web.DynamicSecuredPage;
import ru.ydn.wicket.wicketorientdb.web.OrientDbTestPage;
import ru.ydn.wicket.wicketorientdb.web.StaticSecuredPage;
import static org.junit.Assert.*;

public class TestSecurity
{
	@ClassRule
	public static WicketOrientDbTesterScope wicket = new WicketOrientDbTesterScope();
	
	@After
	public void signOut()
	{
		wicket.getTester().signOut();
	}
	@Test
	public void testSession()
	{
		testSession("admin");
		testSession("reader");
		testSession("writer");
	}
	
	public void testSession(String user)
	{
		testSession(user, user, user);
	}
	
	public void testSession(String userRole, String user, String password)
	{
		IOrientDbSettings settings = wicket.getTester().getApplication().getOrientDbSettings();
		WicketOrientDbTester tester = wicket.getTester();
		
		//Check not signed in state
		assertFalse(tester.getSession().isSignedIn());
		assertNull(tester.getSession().getUser());
		assertNull(tester.getSession().getUsername());
		assertEquals(settings.getGuestUserName(), tester.getDatabaseSession().getUser().getName());
		
		//Signin and check signed in state
		assertTrue(tester.signIn(user, password));
		assertTrue(tester.isSignedIn());
		assertEquals(user, tester.getDatabaseSession().getUser().getName());
		assertEquals(user, tester.getSession().getUser().getName());
		assertTrue(tester.getSession().getRoles().hasRole(userRole));
		
		//Signout and check signed out state
		tester.signOut();
		assertFalse(tester.getSession().isSignedIn());
		assertNull(tester.getSession().getUser());
		assertNull(tester.getSession().getUsername());
		assertEquals(settings.getGuestUserName(), tester.getDatabaseSession().getUser().getName());
	}
	
	
	
	@Test
	public void testTestHomePage() throws Exception
	{
		WicketOrientDbTester tester = wicket.getTester();
		assertEquals(tester.getApplication().getOrientDbSettings().getGuestUserName(), tester.getDatabaseSession().getUser().getName());
		tester.startPage(OrientDbTestPage.class);
		tester.assertRenderedPage(OrientDbTestPage.class);
	}
	
	@Test
	public void testStaticPageForUnsigned() throws Exception
	{
		WicketOrientDbTester tester = wicket.getTester();
		assertEquals(tester.getApplication().getOrientDbSettings().getGuestUserName(), tester.getDatabaseSession().getUser().getName());
		tester.startPage(StaticSecuredPage.class);
		tester.assertRenderedPage(SignInPage.class);
	}
	
	@Test(expected=UnauthorizedInstantiationException.class)
	public void testStaticPageForSigned()
	{
		WicketOrientDbTester tester = wicket.getTester();
		assertEquals(tester.getApplication().getOrientDbSettings().getGuestUserName(), tester.getDatabaseSession().getUser().getName());
		assertTrue(tester.signIn("reader", "reader"));
		tester.startPage(StaticSecuredPage.class);
		tester.signOut();
	}
	
	@Test
	public void testStaticPageForAdmin()
	{
		WicketOrientDbTester tester = wicket.getTester();
		assertEquals(tester.getApplication().getOrientDbSettings().getGuestUserName(), tester.getDatabaseSession().getUser().getName());
		assertTrue(tester.signIn("admin", "admin"));
		tester.startPage(StaticSecuredPage.class);
		tester.assertRenderedPage(StaticSecuredPage.class);
		tester.signOut();
	}

	public void testDynamicPageForUnsigned() throws Exception
	{
		WicketOrientDbTester tester = wicket.getTester();
		assertEquals(tester.getApplication().getOrientDbSettings().getGuestUserName(), tester.getDatabaseSession().getUser().getName());
		tester.startPage(DynamicSecuredPage.class);
		tester.assertRenderedPage(SignInPage.class);
	}
	
//	@Test(expected=UnauthorizedInstantiationException.class)
	public void testDynamicPageForSigned()
	{
		WicketOrientDbTester tester = wicket.getTester();
		assertEquals(tester.getApplication().getOrientDbSettings().getGuestUserName(), tester.getDatabaseSession().getUser().getName());
		assertTrue(tester.signIn("reader", "reader"));
		tester.startPage(DynamicSecuredPage.class);
		assertNull(tester.getLastRenderedPage()); //Due to retries in Wicket 8 we are not getting UnauthorizedInstantiationException
		tester.signOut();
	}
	
	@Test
	public void testDynamicPageForAdmin()
	{
		WicketOrientDbTester tester = wicket.getTester();
		assertEquals(tester.getApplication().getOrientDbSettings().getGuestUserName(), tester.getDatabaseSession().getUser().getName());
		assertTrue(tester.signIn("admin", "admin"));
		tester.startPage(DynamicSecuredPage.class);
		tester.assertRenderedPage(DynamicSecuredPage.class);
		tester.signOut();
	}
}
