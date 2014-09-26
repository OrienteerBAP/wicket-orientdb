package ru.ydn.wicket.wicketorientdb;

import org.junit.Test;
import static org.junit.Assert.*;
import static ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel.buitify;

public class MainUtilsTest
{
	@Test
	public void testBuitify() throws Exception
	{
		assertEquals("Test", buitify("test"));
		assertEquals("Test", buitify("Test"));
		assertEquals("My Test", buitify("myTest"));
		assertEquals("My Test", buitify("my test"));
	}
}
