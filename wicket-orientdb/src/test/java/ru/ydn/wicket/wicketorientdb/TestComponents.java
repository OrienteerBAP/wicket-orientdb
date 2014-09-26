package ru.ydn.wicket.wicketorientdb;

import org.junit.Test;

public class TestComponents extends AbstractTestClass
{
	@Test
	public void testTestHomePage() throws Exception
	{
		wicketTester.startPage(OrientDbTestPage.class);
		wicketTester.assertRenderedPage(OrientDbTestPage.class);
	}
}
