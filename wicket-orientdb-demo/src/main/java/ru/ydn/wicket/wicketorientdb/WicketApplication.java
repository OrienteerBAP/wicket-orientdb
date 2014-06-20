package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 * 
 * @see ru.ydn.wicket.wicketorientdb.Start#main(String[])
 */
public class WicketApplication extends OrientDbWebApplication
{
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return HomePage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();
		getOrientDbSettings().setDBUrl("remote:localhost/TestDocDB");
		getOrientDbSettings().setDefaultUserName("admin");
		getOrientDbSettings().setDefaultUserPassword("admin");
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return SignInPage.class;
	}
}
