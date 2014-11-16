package ru.ydn.wicket.wicketorientdb.junit;

import java.io.Closeable;
import java.io.IOException;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;

import ru.ydn.wicket.wicketorientdb.OrientDbTestWebApplication;

public class WicketTesterThreadLocal extends ThreadLocal<WicketTester>
{
	public static class ClosableWicketTester extends WicketTester implements Closeable
	{

		public ClosableWicketTester(WebApplication application)
		{
			super(application);
		}

		@Override
		public void close() throws IOException {
			destroy();
		}
	}

	@Override
	protected WicketTester initialValue() {
		return new ClosableWicketTester(createWebApplication());
	}
	
	protected WebApplication createWebApplication()
	{
		return new OrientDbTestWebApplication();
	}
	
	@Override
	public void remove() {
		try
		{
			((ClosableWicketTester)get()).close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		super.remove();
	}
	
}
