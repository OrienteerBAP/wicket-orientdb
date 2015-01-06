package ru.ydn.wicket.wicketorientdb.junit;

import org.apache.wicket.util.tester.WicketTester;
import org.apache.wicket.util.tester.WicketTesterScope;

import ru.ydn.wicket.wicketorientdb.OrientDbTestWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

public class WicketOrientDbTesterScope extends WicketTesterScope
{

	@Override
	protected WicketOrientDbTester create() {
		return new WicketOrientDbTester(createApplication());
	}
	
	protected OrientDbWebApplication createApplication()
	{
		return new OrientDbTestWebApplication();
	}

	@Override
	public WicketOrientDbTester getTester() {
		return (WicketOrientDbTester) super.getTester();
	}
}
