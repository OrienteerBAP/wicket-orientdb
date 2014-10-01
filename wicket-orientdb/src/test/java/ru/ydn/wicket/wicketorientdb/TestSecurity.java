package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.authorization.UnauthorizedActionException;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.junit.Before;
import org.junit.Test;

import com.orientechnologies.orient.core.metadata.security.OUser;

import ru.ydn.wicket.wicketorientdb.web.DynamicSecuredPage;
import ru.ydn.wicket.wicketorientdb.web.OrientDbTestPage;
import ru.ydn.wicket.wicketorientdb.web.StaticSecuredPage;
import static org.junit.Assert.*;

public class TestSecurity extends AbstractTestClass
{
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
		IOrientDbSettings settings = getApp().getOrientDbSettings();
		//Check not signed in state
		assertFalse(getSession().isSignedIn());
		assertNull(getSession().getUser());
		assertNull(getSession().getUsername());
		assertEquals(settings.getDBUserName(), getDatabase().getUser().getName());
		
		//Signin and check signed in state
		assertTrue(getSession().signIn(user, password));
		assertTrue(getSession().isSignedIn());
		OUser thisUser = getMetadata().getSecurity().getUser(user);
		assertEquals(thisUser, getSession().getUser());
		assertEquals(thisUser, getDatabase().getUser());
		assertTrue(getSession().getRoles().hasRole(userRole));
		
		//Signout and check signed out state
		getSession().signOut();
		assertFalse(getSession().isSignedIn());
		assertNull(getSession().getUser());
		assertNull(getSession().getUsername());
		assertEquals(settings.getDBUserName(), getDatabase().getUser().getName());
	}
	
	
	
	@Test
	public void testTestHomePage() throws Exception
	{
		wicketTester.startPage(OrientDbTestPage.class);
		wicketTester.assertRenderedPage(OrientDbTestPage.class);
	}
	
	@Test
	public void testStaticPageForUnsigned() throws Exception
	{
		wicketTester.startPage(StaticSecuredPage.class);
		wicketTester.assertRenderedPage(SignInPage.class);
	}
	
	@Test(expected=UnauthorizedInstantiationException.class)
	public void testStaticPageForSigned()
	{
		assertTrue(getSession().signIn("reader", "reader"));
		wicketTester.startPage(StaticSecuredPage.class);
		getSession().signOut();
	}
	
	@Test
	public void testStaticPageForAdmin()
	{
		assertTrue(getSession().signIn("admin", "admin"));
		wicketTester.startPage(StaticSecuredPage.class);
		wicketTester.assertRenderedPage(StaticSecuredPage.class);
		getSession().signOut();
	}

	//TODO: consider this difference with staticly secured pages
	@Test(expected=UnauthorizedActionException.class)
	public void testDynamicPageForUnsigned() throws Exception
	{
		wicketTester.startPage(DynamicSecuredPage.class);
		wicketTester.assertRenderedPage(SignInPage.class);
	}
	
	//TODO: consider this difference with staticly secured pages
	@Test(expected=UnauthorizedActionException.class)
	public void testDynamicPageForSigned()
	{
		assertTrue(getSession().signIn("reader", "reader"));
		wicketTester.startPage(DynamicSecuredPage.class);
		getSession().signOut();
	}
	
	@Test
	public void testDynamicPageForAdmin()
	{
		assertTrue(getSession().signIn("admin", "admin"));
		wicketTester.startPage(DynamicSecuredPage.class);
		wicketTester.assertRenderedPage(DynamicSecuredPage.class);
		getSession().signOut();
	}
}
