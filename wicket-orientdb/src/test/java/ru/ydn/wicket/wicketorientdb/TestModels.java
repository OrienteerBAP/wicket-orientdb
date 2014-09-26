package ru.ydn.wicket.wicketorientdb;

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.orientechnologies.orient.core.metadata.schema.OClass;

import ru.ydn.wicket.wicketorientdb.junit.WicketTesterThreadLocal;
import ru.ydn.wicket.wicketorientdb.model.OClassesDataProvider;

public class TestModels
{
	private final static WicketTesterThreadLocal WICKET_TESTER_THREAD_LOCAL = new WicketTesterThreadLocal();
	
	private WicketTester wicketTester;
	
	@Before
	public void setup()
	{
		wicketTester = WICKET_TESTER_THREAD_LOCAL.get();
	}
	
	@Test
	public void testOClassesDataProvider()
	{
		OClassesDataProvider provider = new OClassesDataProvider();
		provider.setSort("name", SortOrder.ASCENDING);
		assertTrue(provider.size()>5);
		Iterator<? extends OClass> it = provider.iterator(0, 100);
		boolean hasClassA = false;
		boolean hasClassB = false;
		while(it.hasNext())
		{
			OClass oClass = it.next();
			System.out.println("oClass:"+oClass);
			if("ClassA".equals(oClass.getName())) hasClassA=true;
			if("ClassB".equals(oClass.getName())) hasClassB=true;
		}
		assertTrue("ClassA was not found", hasClassA);
		assertTrue("ClassB was not found", hasClassB);
	}
}
