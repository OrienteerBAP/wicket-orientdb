package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;

import ru.ydn.wicket.wicketorientdb.junit.WicketTesterThreadLocal;

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.metadata.OMetadata;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

public class AbstractTestClass
{
	private final static WicketTesterThreadLocal WICKET_TESTER_THREAD_LOCAL = new WicketTesterThreadLocal();
	
	protected WicketTester wicketTester;
	
	@Before
	public void setup()
	{
		wicketTester = WICKET_TESTER_THREAD_LOCAL.get();
	}
	
	public OrientDbWebApplication getApp()
	{
		return OrientDbWebApplication.get();
	}
	
	public OrientDbWebSession getSession()
	{
		return (OrientDbWebSession)wicketTester.getSession();
	}
	
	public ODatabaseRecord getDatabase()
	{
		return getSession().getDatabase();
	}
	
	public OMetadata getMetadata()
	{
		return getDatabase().getMetadata();
	}
	
	public OSchema getSchema()
	{
		return getMetadata().getSchema();
	}
}
