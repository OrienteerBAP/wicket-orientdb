package ru.ydn.wicket.wicketorientdb;

import java.util.ArrayList;
import java.util.List;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.*;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.network.OServerNetworkListener;
import com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpAbstract;

/**
 * Default implementation of {@link IOrientDbSettings}
 */
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

	private OrientDB context;

//	private ODatabasePoolFactory poolFactory;
	
	private List<Class<? extends ORecordHook>> oRecordHooks = new ArrayList<Class<? extends ORecordHook>>();

	public OrientDbSettings() {
		super();
	}

	@Override
	public String getDbName() {
		return dbName;
	}

	@Override
	public ODatabaseType getDbType() {
		return dbType;
	}


//	@Override
//	public ODatabasePoolFactory getDatabasePoolFactory() {
//		return poolFactory;
//	}

	@Override
	public ODatabaseThreadLocalFactory getDatabaseThreadLocalFactory() {
		return Orient.instance().getDatabaseThreadFactory();
	}

//	@Override
//	public void setDatabasePoolFactory(ODatabasePoolFactory poolFactory) {
//		this.poolFactory = poolFactory;
//	}
	

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
		return oRecordHooks;
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
