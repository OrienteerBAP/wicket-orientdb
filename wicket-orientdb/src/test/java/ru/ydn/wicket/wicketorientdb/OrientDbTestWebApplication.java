package ru.ydn.wicket.wicketorientdb;

import com.orientechnologies.orient.core.db.ODatabaseType;
import com.orientechnologies.orient.core.db.OrientDB;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.markup.html.WebPage;
import ru.ydn.wicket.wicketorientdb.web.OrientDbTestPage;

public class OrientDbTestWebApplication extends OrientDbWebApplication
{
	public static final String DB_NAME = "WicketOrientTestDb";
	public static final String DB_MEMORY_URL = "memory:"+DB_NAME;
//	public static final String DB_REMOTE_URL = "remote:localhost/"+DB_NAME;
	@Override
	public void init()
	{
		super.init();
		getApplicationListeners().add(new EmbeddOrientDbApplicationListener(OrientDbTestWebApplication.class.getResource("db.config.xml"))
		{

			@Override
			public void onAfterServerStartupAndActivation(OrientDbWebApplication app) throws Exception {

				OrientDB orientDB = getServer().getContext();
				orientDB.createIfNotExists(getOrientDbSettings().getDbName(), getOrientDbSettings().getDbType());
			}

		});
		getRequestCycleListeners().add(new LazyAuthorizationRequestCycleListener());
		getOrientDbSettings().setDbName(DB_NAME);
		getOrientDbSettings().setDbType(ODatabaseType.MEMORY);
		getOrientDbSettings().setGuestUserName("reader");
		getOrientDbSettings().setGuestPassword("reader");
		getOrientDbSettings().setAdminUserName("admin");
		getOrientDbSettings().setAdminPassword("admin");
		getOrientDbSettings().getORecordHooks().add(TestHook.class);
		getApplicationListeners().add(new TestDataInstallator());
		mountOrientDbRestApi();
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
