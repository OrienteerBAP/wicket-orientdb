package ru.ydn.wicket.wicketorientdb;

import java.util.function.Supplier;

import org.apache.wicket.Application;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IApplicationListener;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IExceptionMapper;
import org.apache.wicket.util.string.Strings;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule.ResourceGeneric;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.server.OServer;

import ru.ydn.wicket.wicketorientdb.converter.HexConverter;
import ru.ydn.wicket.wicketorientdb.converter.ODocumentConverter;
import ru.ydn.wicket.wicketorientdb.converter.OIdentifiableConverter;
import ru.ydn.wicket.wicketorientdb.rest.OrientDBHttpAPIResource;
import ru.ydn.wicket.wicketorientdb.security.IResourceCheckingStrategy;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.WicketOrientDbAuthorizationStrategy;
import ru.ydn.wicket.wicketorientdb.service.ODatabaseHooksInstallListener;
import ru.ydn.wicket.wicketorientdb.utils.FixFormEncTypeListener;
import ru.ydn.wicket.wicketorientdb.utils.FlexyMetaDataKey;
import ru.ydn.wicket.wicketorientdb.utils.ODocumentPropertyLocator;

/**
 * {@link WebApplication} realization for applications on top of OrientDB
 */
public abstract class OrientDbWebApplication extends AuthenticatedWebApplication implements IResourceCheckingStrategy {
	
	private IOrientDbSettings orientDbSettings = new OrientDbSettings();
	private OServer server;
	private Supplier<IExceptionMapper> exceptionMapperProvider = () -> new OrientDefaultExceptionMapper();
	@Override
	protected Class<? extends OrientDbWebSession> getWebSessionClass()
	{
		return OrientDbWebSession.class;
	}
	
	/**
	 * @return settings for the application
	 */
	public IOrientDbSettings getOrientDbSettings()
	{
		return orientDbSettings;
	}
	
	/**
	 * Explicit set of settings for the application. Doesn't recommended to use this method. Consider to use getOrientDBSettings().setXXX()
	 * @param orientDbSettings whole {@link IOrientDbSettings} to be set
	 */
	public void setOrientDbSettings(IOrientDbSettings orientDbSettings)
	{
		this.orientDbSettings=orientDbSettings;
	}
	
	public static OrientDbWebApplication get()
    {
        return (OrientDbWebApplication) WebApplication.get();
    }
	
	public static OrientDbWebApplication lookupApplication()
	{
		return lookupApplication(OrientDbWebApplication.class);
	}
	
	protected static <T extends OrientDbWebApplication> T lookupApplication(Class<T> appClass)
	{
		Application app = Application.exists()?Application.get():null;
		if(app!=null && appClass.isInstance(app)) return (T)app;
		else
		{
			for(String appKey: Application.getApplicationKeys())
			{
				app = Application.get(appKey);
				if(appClass.isInstance(app)) return (T)app;
			}
		}
		return null;
	}

	@Override
	protected void init() {
		super.init();
		OSecurityHelper.init(); //Make sure that FEATURE was loaded
		Orient.instance().registerThreadDatabaseFactory(new DefaultODatabaseThreadLocalFactory(this));
		Orient.instance().addDbLifecycleListener(new ODatabaseHooksInstallListener(this));
		getRequestCycleListeners().add(newTransactionRequestCycleListener());
		getSecuritySettings().setAuthorizationStrategy(new WicketOrientDbAuthorizationStrategy(this, this));
		getApplicationListeners().add(new IApplicationListener() {
			
			
			@Override
			public void onAfterInitialized(Application application) {
				Orient.instance().startup();
				Orient.instance().removeShutdownHook();
			}
			@Override
			public void onBeforeDestroyed(Application application) {
				Orient.instance().shutdown();
			}
		});
		getAjaxRequestTargetListeners().add(new FixFormEncTypeListener());
		//workaround to support changing system users passwords in web interface
		getOrientDbSettings().addORecordHooks(OUserCatchPasswordHook.class);
		PropertyResolver.setLocator(this, new ODocumentPropertyLocator(new PropertyResolver.CachingPropertyLocator(new PropertyResolver.DefaultPropertyLocator())));
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
		locator.set(byte[].class, new HexConverter());
		return locator;
	}

	public OServer getServer() {
		return server;
	}

	public void setServer(OServer server) {
		this.server = server;
	}

	protected void mountOrientDbRestApi()
	{
		OrientDBHttpAPIResource.mountOrientDbRestApi(this);
	}
	
	public String getOrientDBVersion() 
	{
		return Orient.class.getPackage().getImplementationVersion();
	}
	
	@Override
	public boolean checkResource(ResourceGeneric resource, String specific, int iOperation) {
		OSecurityUser user = OrientDbWebSession.get().getEffectiveUser();
		if(Strings.isEmpty(specific)) specific = null;
		if(user.checkIfAllowed(resource, specific, iOperation)!=null) return true;
		while(!Strings.isEmpty(specific=Strings.beforeLastPathComponent(specific, '.')))
		{
			if(user.checkIfAllowed(resource, specific+"."+ODatabaseSecurityResources.ALL, iOperation)!=null) return true;
		}
		return false;
	}
	
	@Override
	public Supplier<IExceptionMapper> getExceptionMapperProvider() {
		return exceptionMapperProvider;
	}
	
	protected void setExceptionMapperProvider(Supplier<IExceptionMapper> exceptionMapperProvider) {
		this.exceptionMapperProvider = exceptionMapperProvider!=null?exceptionMapperProvider:super.getExceptionMapperProvider();
	}

	public final synchronized <K, V> V getMetaData(final K key)
	{
		return FlexyMetaDataKey.get(this, key);
	}
	
	public final synchronized <K, V> OrientDbWebApplication setMetaData(final K key, final V value)
	{
		FlexyMetaDataKey.set(this, key, value);
		return this;
	}
	
}
