package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage
{
	private static WicketTester tester;

	@BeforeClass
	public static void setUp()
	{
		tester = new WicketTester(new WicketApplication());
	}
	
	@AfterClass
	public static void shutdown()
	{
		tester.destroy();
	}

	@Test
	public void homepageRendersSuccessfully()
	{
		//start and render the test page
		tester.startPage(HomePage.class);

		//assert rendered page class
		tester.assertRenderedPage(HomePage.class);
	}
}
