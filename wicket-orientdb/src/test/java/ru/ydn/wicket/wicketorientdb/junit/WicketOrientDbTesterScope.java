package ru.ydn.wicket.wicketorientdb.junit;

import org.apache.wicket.util.tester.WicketTesterScope;
import ru.ydn.wicket.wicketorientdb.OrientDbTestWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

public class WicketOrientDbTesterScope extends WicketTesterScope
{
	private String username;
	private String password;
	
	public WicketOrientDbTesterScope() {
		this(null, null);
	}
	
	public WicketOrientDbTesterScope(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	protected WicketOrientDbTester create() {
		return new WicketOrientDbTester(createApplication(), username, password);
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
