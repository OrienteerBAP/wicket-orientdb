package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.markup.html.WebPage;

import ru.ydn.wicket.wicketorientdb.web.OrientDbTestPage;

import com.orientechnologies.orient.client.remote.OServerAdmin;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

public class OrientDbTestWebApplication extends OrientDbWebApplication
{
	public static final String DB_NAME = "WicketOrientTestDb";
	@Override
	public void init()
	{
		super.init();
		getApplicationListeners().add(new EmbeddOrientDbApplicationListener(OrientDbTestWebApplication.class.getResource("db.config.xml"))
		{

			@Override
			public void onAfterServerStartupAndActivation(OrientDbWebApplication app) throws Exception {
				IOrientDbSettings settings = app.getOrientDbSettings();
				ODatabaseDocumentTx db = new ODatabaseDocumentTx(settings.getDBUrl());
				if(!db.exists()) db = db.create();
				if(db.isClosed()) db.open(settings.getDBInstallatorUserName(), settings.getDBInstallatorUserPassword());
				db.getMetadata().load();
				db.close();
			}
			
		});
		getOrientDbSettings().setDBUrl("memory:"+DB_NAME);
		getOrientDbSettings().setDBUserName("admin");
		getOrientDbSettings().setDBUserPassword("admin");
		getOrientDbSettings().setDBInstallatorUserName("admin");
		getOrientDbSettings().setDBInstallatorUserPassword("admin");
		getApplicationListeners().add(new TestDataInstallator());
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return SignInPage.class;
	}

	@Override
	public Class<? extends Page> getHomePage() {
		return OrientDbTestPage.class;
	}

}
