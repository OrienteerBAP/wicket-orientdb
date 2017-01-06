package ru.ydn.wicket.wicketorientdb.demo;

import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.markup.html.WebPage;

import ru.ydn.wicket.wicketorientdb.EmbeddOrientDbApplicationListener;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 * 
 * @see ru.ydn.wicket.wicketorientdb.demo.Start#main(String[])
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
			public void onAfterServerStartupAndActivation(OrientDbWebApplication app) throws Exception {
				
				IOrientDbSettings settings = app.getOrientDbSettings();
				ODatabaseDocumentTx db = new ODatabaseDocumentTx(settings.getDBUrl());
				if(!db.exists()) db = db.create();
				if(db.isClosed()) db.open(settings.getAdminUserName(), settings.getAdminPassword());
				db.getMetadata().load();
				db.close();
			}
			
		});
		getOrientDbSettings().setDBUrl("memory:"+DB_NAME);
		getOrientDbSettings().setGuestUserName("admin");
		getOrientDbSettings().setGuestPassword("admin");
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return SignInPage.class;
	}
}
