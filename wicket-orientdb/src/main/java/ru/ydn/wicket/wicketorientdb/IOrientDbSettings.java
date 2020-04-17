package ru.ydn.wicket.wicketorientdb;

import java.util.List;

import com.orientechnologies.orient.core.db.ODatabasePoolBase;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;
import com.orientechnologies.orient.core.db.OPartitionedDatabasePoolFactory;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.hook.ORecordHook;

/**
 * OrientDB setting to be used in Wicket-OrientDB application
 */
public interface IOrientDbSettings {
	/**
	 * @return URL to connect to the OrientDB
	 */
	public String getDBUrl();
	/**
	 * @return Default DB username
	 */
	public String getGuestUserName();
	/**
	 * @return Password for default user
	 */
	public String getGuestPassword();
	/**
	 * @return Username for user which should be used for administrative tasks
	 */
	public String getAdminUserName();
	/**
	 * @return Password for the user which should be used for administrative tasks
	 */
	public String getAdminPassword();
	/**
	 * @return {@link OPartitionedDatabasePoolFactory} for DB pool for the application
	 */
	public OPartitionedDatabasePoolFactory getDatabasePoolFactory();
	/**
	 * @return factory for {@link ODatabaseDocument}
	 */
	public ODatabaseThreadLocalFactory getDatabaseThreadLocalFactory();
	
	/**
	 * @return default url for orientdb rest API
	 */
	public String getOrientDBRestApiUrl();
	/**
	 * Set URL for the OrientDB
	 * @param url URL of OrientDB database
	 */
	public void setDBUrl(String url);
	/**
	 * Set username for default user
	 * @param userName default username for login
	 */
	public void setGuestUserName(String userName);
	/**
	 * Set password for default user
	 * @param password password for default user
	 */
	public void setGuestPassword(String password);
	/**
	 * Set username for user which will be used for admin stuff
	 * @param userName username of a user with super rights for DB installation
	 */
	public void setAdminUserName(String userName);
	/**
	 * Set password for user which will be used for admin stuff
	 * @param password password of a user with super rights
	 */
	public void setAdminPassword(String password);
	/**
	 * Set {@link ODatabasePoolBase} which should be used for DB connections pooling
	 * @param poolFactory setup a poolFactory
	 */
	public void setDatabasePoolFactory(OPartitionedDatabasePoolFactory poolFactory);
	/**
	 * Set {@link ODatabaseThreadLocalFactory} which should be used for obtaining {@link ODatabaseDocument}
	 * @param factory factory of a {@link ThreadLocal} db
	 */
	public void setDatabaseThreadLocalFactory(ODatabaseThreadLocalFactory factory);
	/**
	 * Set OrientDB Rest API URL
	 * @param orientDbRestApiUrl URL of orientDB REST API
	 */
	public void setOrientDBRestApiUrl(String orientDbRestApiUrl);
	/**
	 * @return {@link List} of {@link ORecordHook} which should be registered for every DB instance created
	 */
	public List<Class<? extends ORecordHook>> getORecordHooks();
}
