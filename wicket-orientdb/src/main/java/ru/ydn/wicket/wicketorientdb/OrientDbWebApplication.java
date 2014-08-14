package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.protocol.http.WebApplication;

import ru.ydn.wicket.wicketorientdb.security.WicketOrientDbAuthorizationStrategy;

import com.orientechnologies.orient.core.Orient;

public abstract class OrientDbWebApplication extends AuthenticatedWebApplication {
	private IOrientDbSettings orientDbSettings = new OrientDbSettings();

	@Override
	protected Class<? extends OrientDbWebSession> getWebSessionClass()
	{
		return OrientDbWebSession.class;
	}
	
	public IOrientDbSettings getOrientDbSettings()
	{
		return orientDbSettings;
	}
	
	public void setOrientDbSettings(IOrientDbSettings orientDbSettings)
	{
		this.orientDbSettings=orientDbSettings;
	}
	
	public static OrientDbWebApplication get()
    {
        return (OrientDbWebApplication) WebApplication.get();
    }

	@Override
	protected void init() {
		super.init();
		Orient.instance().registerThreadDatabaseFactory(new DefaultODatabaseThreadLocalFactory(this));
		getRequestCycleListeners().add(newTransactionRequestCycleListener());
		getRequestCycleListeners().add(new OrientDefaultExceptionsHandlingListener());
		getSecuritySettings().setAuthorizationStrategy(new WicketOrientDbAuthorizationStrategy(this));
		getApplicationListeners().add(new IApplicationListener() {
			
			
			@Override
			public void onAfterInitialized(Application application) {
				Orient.instance().removeShutdownHook();
			}
			@Override
			public void onBeforeDestroyed(Application application) {
				Orient.instance().shutdown();
			}
		});
	}
	
	protected TransactionRequestCycleListener newTransactionRequestCycleListener()
	{
		return new TransactionRequestCycleListener();
	}

}
