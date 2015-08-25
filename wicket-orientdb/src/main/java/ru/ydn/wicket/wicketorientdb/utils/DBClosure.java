package ru.ydn.wicket.wicketorientdb.utils;

import java.io.Serializable;

import ru.ydn.wicket.wicketorientdb.DefaultODatabaseThreadLocalFactory;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.OPartitionedDatabasePoolFactory;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;

/**
 * Closure for execution of portion queries/command on database for different user (commonly, under admin)
 * @param <V>
 */
public abstract class DBClosure<V> implements Serializable
{
	private static final long serialVersionUID = 1L;
	private final String dbUrl;
	private final String username;
	private final String password;
	
	public DBClosure()
	{
		this(null, null, null);
	}
	
	public DBClosure(String username, String password)
	{
		this(null, username, password);
	}
	
	public DBClosure(String dbUrl, String username, String password)
	{
		this.dbUrl = dbUrl;
		this.username = username;
		this.password = password;
	}
	/**
	 * @return result of execution
	 */
	public final V execute()
	{
		ODatabaseDocument db = null;
		ODatabaseDocument oldDb = ODatabaseRecordThreadLocal.INSTANCE.getIfDefined();
		if(oldDb!=null) ODatabaseRecordThreadLocal.INSTANCE.remove(); //Required to avoid stack of transactions
		try
		{
			db = getSettings().getDatabasePoolFactory().get(getDBUrl(), getUsername(), getPassword()).acquire();
			db.activateOnCurrentThread();
			return execute(db);
		} 
		finally
		{
			if(db!=null) db.close();
			if(oldDb!=null) ODatabaseRecordThreadLocal.INSTANCE.set((ODatabaseDocumentInternal)oldDb);
			else ODatabaseRecordThreadLocal.INSTANCE.remove();
		}
	}
	
	protected String getDBUrl()
	{
		return dbUrl!=null?dbUrl:getSettings().getDBUrl();
	}
	
	protected String getUsername()
	{
		return username!=null?username:getSettings().getDBInstallatorUserName();
	}
	
	protected String getPassword()
	{
		return password!=null?password:getSettings().getDBInstallatorUserPassword();
	}
	
	protected IOrientDbSettings getSettings()
	{
		return OrientDbWebApplication.get().getOrientDbSettings();
	}
	
	/**
	 * @param db temporal DB for other user
	 * @return results for execution on supplied DB
	 */
	protected abstract V execute(ODatabaseDocument db);
}
