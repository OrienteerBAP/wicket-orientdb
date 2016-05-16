package ru.ydn.wicket.wicketorientdb;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Application;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IApplicationListener;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.IPackageResourceGuard;
import org.apache.wicket.markup.html.SecurePackageResourceGuard;
import org.apache.wicket.protocol.http.WebApplication;

import ru.ydn.wicket.wicketorientdb.converter.ODocumentConverter;
import ru.ydn.wicket.wicketorientdb.converter.OIdentifiableConverter;
import ru.ydn.wicket.wicketorientdb.rest.OrientDBHttpAPIResource;
import ru.ydn.wicket.wicketorientdb.security.WicketOrientDbAuthorizationStrategy;

import com.google.common.collect.Collections2;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.ODatabaseLifecycleListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.ORule.ResourceGeneric;
import com.orientechnologies.orient.core.metadata.security.OSecurity;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.server.OServer;

/**
 * {@link WebApplication} realization for applications on top of OrientDB
 */
public abstract class OrientDbWebApplication extends AuthenticatedWebApplication {
	
	private IOrientDbSettings orientDbSettings = new OrientDbSettings();
	private OServer server;

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
		Application app = Application.get();
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
		Orient.instance().registerThreadDatabaseFactory(new DefaultODatabaseThreadLocalFactory(this));
		Orient.instance().addDbLifecycleListener(new ODatabaseLifecycleListener() {
			
			private ORecordHook createHook(Class<? extends ORecordHook> clazz, ODatabaseInternal iDatabase) {
				if(!(iDatabase instanceof ODatabaseDocument)) return null;
				try {
					return (ORecordHook) clazz.getConstructor(ODatabaseDocument.class).newInstance(iDatabase);
				} catch (Exception e) {
					try {
						return (ORecordHook) clazz.newInstance();
					} catch (Exception e1) {
						throw new IllegalStateException("Can't initialize hook "+clazz.getName(), e);
					}
				}
			}
			@Override
			public void onOpen(ODatabaseInternal iDatabase) {
				registerHooks(iDatabase);
			}
			
			@Override
			public void onCreate(ODatabaseInternal iDatabase) {
				registerHooks(iDatabase);
				//Fix for "feature" appeared in OrientDB 2.1.1
				//Issue: https://github.com/orientechnologies/orientdb/issues/4906
				OSecurity security = iDatabase.getMetadata().getSecurity();
				ORole readerRole = security.getRole("reader");
				readerRole.grant(ResourceGeneric.CLUSTER, "orole", ORole.PERMISSION_READ);
				readerRole.grant(ResourceGeneric.CLUSTER, "ouser", ORole.PERMISSION_READ);
				readerRole.grant(ResourceGeneric.CLASS, "orole", ORole.PERMISSION_READ);
				readerRole.grant(ResourceGeneric.CLASS, "ouser", ORole.PERMISSION_READ);
				readerRole.save();
				ORole writerRole = security.getRole("writer");
				writerRole.grant(ResourceGeneric.CLUSTER, "orole", ORole.PERMISSION_READ);
				writerRole.grant(ResourceGeneric.CLUSTER, "ouser", ORole.PERMISSION_READ);
				writerRole.grant(ResourceGeneric.CLASS, "orole", ORole.PERMISSION_READ);
				writerRole.grant(ResourceGeneric.CLASS, "ouser", ORole.PERMISSION_READ);
				writerRole.save();
			}
			
			public void registerHooks(ODatabaseInternal iDatabase) {
				Set<ORecordHook> hooks = iDatabase.getHooks().keySet();
				List<Class<? extends ORecordHook>> hooksToRegister = new ArrayList<Class<? extends ORecordHook>>(getOrientDbSettings().getORecordHooks());
				for(ORecordHook hook : hooks)
				{
					if(hooksToRegister.contains(hook.getClass())) hooksToRegister.remove(hook.getClass());
				}
				for (Class<? extends ORecordHook> oRecordHookClass : hooksToRegister)
				{
					ORecordHook hook = createHook(oRecordHookClass, iDatabase);
					if(hook!=null) iDatabase.registerHook(hook);
				}
			}
			
			@Override
			public void onClose(ODatabaseInternal iDatabase) {/*NOP*/}
			
			@Override
			public void onDrop(ODatabaseInternal iDatabase) {/*NOP*/}
			
			
			public PRIORITY getPriority() {
				return PRIORITY.REGULAR;
			}

			@Override
			public void onCreateClass(ODatabaseInternal iDatabase, OClass iClass) {/*NOP*/}

			@Override
			public void onDropClass(ODatabaseInternal iDatabase, OClass iClass) {/*NOP*/}
		});
		getRequestCycleListeners().add(newTransactionRequestCycleListener());
		getRequestCycleListeners().add(new OrientDefaultExceptionsHandlingListener());
		getSecuritySettings().setAuthorizationStrategy(new WicketOrientDbAuthorizationStrategy(this));
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
	
}
