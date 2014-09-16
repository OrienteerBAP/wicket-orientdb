package ru.ydn.wicket.wicketorientdb;

import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IApplicationListener;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.protocol.http.WebApplication;

import ru.ydn.wicket.wicketorientdb.converter.ODocumentConverter;
import ru.ydn.wicket.wicketorientdb.converter.OIdentifiableConverter;
import ru.ydn.wicket.wicketorientdb.security.WicketOrientDbAuthorizationStrategy;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseComplex;
import com.orientechnologies.orient.core.db.ODatabaseLifecycleListener;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.record.impl.ODocument;

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
		Orient.instance().addDbLifecycleListener(new ODatabaseLifecycleListener() {
			
			@Override
			public void onOpen(ODatabase iDatabase) {
				for (ORecordHook oRecordHook : getOrientDbSettings().getORecordHooks())
				{
					((ODatabaseComplex<?>)iDatabase).registerHook(oRecordHook);
				}
			}
			
			@Override
			public void onCreate(ODatabase iDatabase) {
				for (ORecordHook oRecordHook : getOrientDbSettings().getORecordHooks())
				{
					((ODatabaseComplex<?>)iDatabase).registerHook(oRecordHook);
				}
			}
			
			@Override
			public void onClose(ODatabase iDatabase) {
				for (ORecordHook oRecordHook : getOrientDbSettings().getORecordHooks())
				{
					((ODatabaseComplex<?>)iDatabase).unregisterHook(oRecordHook);
				}
			}
		});
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
	
	@Override
	protected IConverterLocator newConverterLocator()
	{
		ConverterLocator locator =  new ConverterLocator();
		locator.set(OIdentifiable.class, new OIdentifiableConverter<OIdentifiable>());
		locator.set(ODocument.class, new ODocumentConverter());
		return locator;
	}

}
