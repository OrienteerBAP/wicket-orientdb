package ru.ydn.wicket.wicketorientdb.utils;

import java.io.Serializable;

import ru.ydn.wicket.wicketorientdb.DefaultODatabaseThreadLocalFactory;
import ru.ydn.wicket.wicketorientdb.IOrientDbSettings;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

public abstract class DBClosure<V> implements Serializable
{
	private final String dbUrl;
	private final String username;
	private final String password;
	
	public DBClosure()
	{
		IOrientDbSettings settings = getSettings();
		this.dbUrl = settings.getDBUrl();
		this.username = settings.getDBInstallatorUserName();
		this.password = settings.getDBInstallatorUserPassword();
	}
	
	public DBClosure(String username, String password)
	{
		this(getSettings().getDBUrl(), username, password);
	}
	
	public DBClosure(String dbUrl, String username, String password)
	{
		this.dbUrl = dbUrl;
		this.username = username;
		this.password = password;
	}
	
	public final V execute()
	{
		ODatabaseRecord db = null;
		ODatabaseRecord oldDb = ODatabaseRecordThreadLocal.INSTANCE.getIfDefined();
		try
		{
			db = DefaultODatabaseThreadLocalFactory.castToODatabaseRecord(getSettings().getDatabasePool().acquire(dbUrl, username, password));
			return execute(db);
		} 
		finally
		{
			if(db!=null) db.close();
			if(oldDb!=null) ODatabaseRecordThreadLocal.INSTANCE.set(oldDb);
			else ODatabaseRecordThreadLocal.INSTANCE.remove();
		}
	}
	
	protected static IOrientDbSettings getSettings()
	{
		return OrientDbWebApplication.get().getOrientDbSettings();
	}
	
	protected abstract V execute(ODatabaseRecord db);
}
