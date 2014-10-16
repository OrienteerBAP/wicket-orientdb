package ru.ydn.wicket.wicketorientdb;

import java.util.List;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabasePoolBase;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
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
	public String getDBUserName();
	/**
	 * @return Password for default user
	 */
	public String getDBUserPassword();
	/**
	 * @return Username for user which should be used for administrative tasks
	 */
	public String getDBInstallatorUserName();
	/**
	 * @return Password for the user which should be used for administrative tasks
	 */
	public String getDBInstallatorUserPassword();
	/**
	 * @return {@link ODatabasePoolBase} for DB pool for the application
	 */
	public ODatabasePoolBase<? extends ODatabase> getDatabasePool();
	/**
	 * @return factory for {@link ODatabaseRecord}
	 */
	public ODatabaseThreadLocalFactory getDatabaseThreadLocalFactory();
	/**
	 * Set URL for the OrientDB
	 * @param url
	 */
	public void setDBUrl(String url);
	/**
	 * Set username for default user
	 * @param userName
	 */
	public void setDBUserName(String userName);
	/**
	 * Set password for default user
	 * @param password
	 */
	public void setDBUserPassword(String password);
	/**
	 * Set username for user which will be used for admin stuff
	 * @param userName
	 */
	public void setDBInstallatorUserName(String userName);
	/**
	 * Set password for user which will be used for admin stuff
	 * @param password
	 */
	public void setDBInstallatorUserPassword(String password);
	/**
	 * Set {@link ODatabasePoolBase} which should be used for DB connections pooling
	 * @param pool
	 */
	public void setDatabasePool(ODatabasePoolBase<? extends ODatabase> pool);
	/**
	 * Set {@link ODatabaseThreadLocalFactory} which should be used for obtaining {@link ODatabaseRecord}
	 * @param factory
	 */
	public void setDatabaseThreadLocalFactory(ODatabaseThreadLocalFactory factory);
	/**
	 * @return {@link List} of {@link ORecordHook} which should be registered for every DB instance created
	 */
	public List<ORecordHook> getORecordHooks();
}
