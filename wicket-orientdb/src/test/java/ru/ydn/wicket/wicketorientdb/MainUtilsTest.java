package ru.ydn.wicket.wicketorientdb;

import org.junit.Test;

import com.google.common.base.Converter;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

import ru.ydn.wicket.wicketorientdb.utils.OClassClassNameConverter;
import ru.ydn.wicket.wicketorientdb.utils.OIndexNameConverter;
import ru.ydn.wicket.wicketorientdb.utils.OPropertyFullNameConverter;
import static org.junit.Assert.*;
import static ru.ydn.wicket.wicketorientdb.model.AbstractNamingModel.buitify;

public class MainUtilsTest extends AbstractTestClass
{
	@Test
	public void testBuitify() throws Exception
	{
		assertEquals("Test", buitify("test"));
		assertEquals("Test", buitify("Test"));
		assertEquals("My Test", buitify("myTest"));
		assertEquals("My Test", buitify("my test"));
	}
	
	@Test
	public void testConverters() throws Exception
	{
		testConverter(OClassClassNameConverter.INSTANCE, getSchema().getClass("OUser"), "OUser");
		testConverter(OPropertyFullNameConverter.INSTANCE, getSchema().getClass("Ouser").getProperty("name"), "OUser.name");
		testConverter(OIndexNameConverter.INSTANCE, getSchema().getClass("Ouser").getClassIndex("OUser.name"), "OUser.name");
	}
	
	public <F, T> void testConverter(Converter<F, T> converter, F fromObject, T toObject)
	{
		assertEquals(toObject, converter.convert(fromObject));
		assertEquals(fromObject, converter.reverse().convert(toObject));
	}
}
