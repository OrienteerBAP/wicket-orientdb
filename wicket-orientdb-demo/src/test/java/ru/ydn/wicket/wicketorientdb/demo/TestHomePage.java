package ru.ydn.wicket.wicketorientdb.demo;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.ydn.wicket.wicketorientdb.demo.HomePage;
import ru.ydn.wicket.wicketorientdb.demo.WicketApplication;

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
