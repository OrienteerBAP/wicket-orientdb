package ru.ydn.wicket.wicketorientdb;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseComplex;
import com.orientechnologies.orient.core.db.ODatabasePoolBase;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

public class OrientDbSettings implements IOrientDbSettings
{
	private String dbUrl;
	private String defaultUserName;
	private String defaultUserPassword;
	private ODatabasePoolBase<? extends ODatabase> pool = ODatabaseDocumentPool.global();
	public final ODatabaseThreadLocalFactory DEFAULT_DATABASE_THREAD_LOCAL_FACTORY 
									= new ODatabaseThreadLocalFactory() {
									
									@Override
									@SuppressWarnings({ "resource", "rawtypes" })
									public ODatabaseRecord getThreadDatabase() {
										ODatabase db = pool.acquire(dbUrl, defaultUserName, defaultUserPassword);
										while(db!=null && !(db instanceof ODatabaseRecord))
										{
											if(db instanceof ODatabaseComplex)
											{
												db = ((ODatabaseComplex)db).getUnderlying();
											}
										}
										return (ODatabaseRecord)db;
									}
								};
	
	@Override
	public String getDBUrl() {
		return dbUrl;
	}

	@Override
	public String getDefaultUserName() {
		return defaultUserName;
	}

	@Override
	public String getDefaultUserPassword() {
		return defaultUserPassword;
	}

	@Override
	public ODatabasePoolBase<? extends ODatabase> getDatabasePool() {
		return pool;
	}

	@Override
	public ODatabaseThreadLocalFactory getDatabaseThreadLocalFactory() {
		return Orient.instance().getDatabaseThreadFactory();
	}

	@Override
	public void setDBUrl(String url) {
		this.dbUrl = url;
	}

	@Override
	public void setDefaultUserName(String userName) {
		this.defaultUserName=userName;
	}

	@Override
	public void setDefaultUserPassword(String password) {
		this.defaultUserPassword=password;
	}

	@Override
	public void setDatabasePool(ODatabasePoolBase<? extends ODatabase> pool) {
		this.pool = pool;
	}

	@Override
	public void setDatabaseThreadLocalFactory(
			ODatabaseThreadLocalFactory factory) {
		Orient.instance().registerThreadDatabaseFactory(factory);
	}

}
