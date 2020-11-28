package ru.ydn.wicket.wicketorientdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.*;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.network.OServerNetworkListener;
import com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpAbstract;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import ru.ydn.wicket.wicketorientdb.rest.DynamicInterceptor;
import ru.ydn.wicket.wicketorientdb.rest.WicketSessionCookieJar;
import ru.ydn.wicket.wicketorientdb.utils.ODbUtils;

/**
 * Default implementation of {@link IOrientDbSettings}
 */
@Slf4j
public class OrientDbSettings implements IOrientDbSettings
{
	public static final String ADMIN_DEFAULT_USERNAME = "admin";
	public static final String ADMIN_DEFAULT_PASSWORD = "admin";
	public static final String READER_DEFAULT_USERNAME = "reader";
	public static final String READER_DEFAULT_PASSWORD = "reader";

	private String dbName;
	private ODatabaseType dbType;
	private String guestUserName=READER_DEFAULT_USERNAME;
	private String guestPassword=READER_DEFAULT_PASSWORD;
	private String adminUserName=ADMIN_DEFAULT_USERNAME;
	private String adminPassword=ADMIN_DEFAULT_PASSWORD;
	private String orientDbRestApiUrl;
	private OkHttpClient okHttpClient;

	private OrientDB context;

	private List<Class<? extends ORecordHook>> oRecordHooks;
	private List<Class<? extends ORecordHook>> unmodifiableORecordHooks;

	public OrientDbSettings() {
		super();
		oRecordHooks = new ArrayList<Class<? extends ORecordHook>>();
		unmodifiableORecordHooks = Collections.unmodifiableList(oRecordHooks);
	}

	@Override
	public String getDbName() {
		return dbName;
	}

	@Override
	public ODatabaseType getDbType() {
		return dbType;
	}

	@Override
	public ODatabaseThreadLocalFactory getDatabaseThreadLocalFactory() {
		return Orient.instance().getDatabaseThreadFactory();
	}

	@Override
	public String getGuestUserName() {
		return guestUserName;
	}

	@Override
	public String getGuestPassword() {
		return guestPassword;
	}

	@Override
	public void setGuestUserName(String userName) {
		this.guestUserName = userName;
	}

	@Override
	public void setGuestPassword(String password) {
		this.guestPassword = password;
	}
	
	@Override
	public String getAdminUserName() {
		return adminUserName;
	}


	@Override
	public String getAdminPassword() {
		return adminPassword;
	}


	@Override
	public void setAdminUserName(String userName) {
		this.adminUserName = userName;
	}


	@Override
	public void setAdminPassword(String password) {
		this.adminPassword = password;
	}


	@Override
	public void setDatabaseThreadLocalFactory(
			ODatabaseThreadLocalFactory factory) {
		Orient.instance().registerThreadDatabaseFactory(factory);
	}


	@Override
	public List<Class<? extends ORecordHook>> getORecordHooks() {
		return unmodifiableORecordHooks;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addORecordHooks(Class<? extends ORecordHook>... classes) {
		List<Class<? extends ORecordHook>> hooks = Arrays.asList(classes);
		oRecordHooks.addAll(hooks);
		ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().getIfDefined();
		if (db != null) {
			ODbUtils.registerHooks((ODatabaseInternal<?>) db, hooks);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void removeORecordHooks(Class<? extends ORecordHook>... classes) {
		List<Class<? extends ORecordHook>> hooks = Arrays.asList(classes);
		oRecordHooks.removeAll(hooks);
		ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().getIfDefined();
		if (db != null) {
			ODbUtils.unregisterHooks((ODatabaseInternal<?>) db, hooks);
		}
	}

	@Override
	public OrientDB getContext() {
		return context;
	}

	@Override
	public void setContext(OrientDB orientDB) {
		this.context = orientDB;
	}


	@Override
	public String getOrientDBRestApiUrl() {
		if(orientDbRestApiUrl==null)
		{
			setOrientDBRestApiUrl(resolveOrientDBRestApiUrl());
		}
		return orientDbRestApiUrl;
	}
	
	@Override
	public OkHttpClient getOkHttpClient() {
		if(okHttpClient==null) {
			setOkHttpClient(new OkHttpClient.Builder()
					.cookieJar(new WicketSessionCookieJar())
					.addInterceptor(DynamicInterceptor.INSTANCE)
					.addNetworkInterceptor(DynamicInterceptor.INSTANCE)
					.build());
		}
		return okHttpClient;
	}

	@Override
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	@Override
	public void setDbType(ODatabaseType dbType) {
		this.dbType = dbType;
	}

	@Override
	public void setDbType(String dbType) {
		setDbType(ODatabaseType.valueOf(dbType.toUpperCase()));
	}


	@Override
	public void setOrientDBRestApiUrl(String orientDbRestApiUrl) {
		if(orientDbRestApiUrl!=null && !orientDbRestApiUrl.endsWith("/")) orientDbRestApiUrl+="/";
		this.orientDbRestApiUrl = orientDbRestApiUrl;
	}
	
	@Override
	public void setOkHttpClient(OkHttpClient okHttpClient) {
		this.okHttpClient = okHttpClient;
	}
	
	/**
	 * Resolve OrientDB REST API URL to be used for OrientDb REST bridge
	 * @return OrientDB REST API URL
	 */
	public String resolveOrientDBRestApiUrl()
	{
		OrientDbWebApplication app = OrientDbWebApplication.get();
		OServer server = app.getServer();
		if(server!=null)
		{
			OServerNetworkListener http = server.getListenerByProtocol(ONetworkProtocolHttpAbstract.class);
			if(http!=null)
			{
				return "http://"+http.getListeningAddress(true);
			}
		}
		return null;
	}

}
