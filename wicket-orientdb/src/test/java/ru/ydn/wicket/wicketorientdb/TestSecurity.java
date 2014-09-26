package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.authorization.UnauthorizedActionException;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.junit.Before;
import org.junit.Test;

import ru.ydn.wicket.wicketorientdb.web.DynamicSecuredPage;
import ru.ydn.wicket.wicketorientdb.web.OrientDbTestPage;
import ru.ydn.wicket.wicketorientdb.web.StaticSecuredPage;
import static org.junit.Assert.*;

public class TestSecurity extends AbstractTestClass
{
	@Test
	public void testSession()
	{
		assertFalse(getSession().isSignedIn());
		assertNull(getSession().getUser());
		assertNull(getSession().getUsername());
		assertTrue(getSession().signIn("admin", "admin"));
		assertTrue(getSession().isSignedIn());
		assertEquals(getMetadata().getSecurity().getUser("admin"), getSession().getUser());
		assertTrue(getSession().getRoles().hasRole("admin"));
		getSession().signOut();
		assertFalse(getSession().isSignedIn());
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
