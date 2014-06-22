package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

import com.orientechnologies.orient.client.remote.OServerAdmin;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 * 
 * @see ru.ydn.wicket.wicketorientdb.Start#main(String[])
 */
public class WicketApplication extends OrientDbWebApplication
{
	public static final String DB_NAME = "WicketOrientDb";
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
		getApplicationListeners().add(new EmbeddOrientDbApplicationListener(WicketApplication.class.getResource("db.config.xml"))
		{

			@Override
			public void onAfterServerStartupAndActivation() throws Exception {
				OServerAdmin serverAdmin = new OServerAdmin("localhost/"+DB_NAME).connect("root", "WicketOrientDB");
				if(!serverAdmin.existsDatabase())
			    serverAdmin.createDatabase(DB_NAME, "graph", "local");
			    
			}
			
		});
		getOrientDbSettings().setDBUrl("local:localhost/"+DB_NAME);
		getOrientDbSettings().setDefaultUserName("admin");
		getOrientDbSettings().setDefaultUserPassword("admin");
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return SignInPage.class;
	}
}
