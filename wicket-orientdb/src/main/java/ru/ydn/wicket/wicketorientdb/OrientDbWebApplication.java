package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;

import ru.ydn.wicket.wicketorientdb.security.WicketOrientDbAuthorizationStrategy;

import com.orientechnologies.orient.core.Orient;

public abstract class OrientDbWebApplication extends AuthenticatedWebApplication {
	private final OrientDbSettings orientDbSettings = new OrientDbSettings();

	@Override
	protected Class<? extends OrientDbWebSession> getWebSessionClass()
	{
		return OrientDbWebSession.class;
	}
	
	public IOrientDbSettings getOrientDbSettings()
	{
		return orientDbSettings;
	}

	@Override
	protected void init() {
		super.init();
		Orient.instance().registerThreadDatabaseFactory(orientDbSettings.DEFAULT_DATABASE_THREAD_LOCAL_FACTORY);
		getRequestCycleListeners().add(newTransactionRequestCycleListener());
		getSecuritySettings().setAuthorizationStrategy(new WicketOrientDbAuthorizationStrategy(this));
	}
	
	protected TransactionRequestCycleListener newTransactionRequestCycleListener()
	{
		return new TransactionRequestCycleListener();
	}

}
