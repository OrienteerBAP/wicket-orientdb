package ru.ydn.wicket.wicketorientdb;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabasePoolBase;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;

public class OrientDbSettings implements IOrientDbSettings
{
	private String dbUrl;
	private String dbUserName;
	private String dbUserPassword;
	private String defaultUserName;
	private String defaultUserPassword;
	private ODatabasePoolBase<? extends ODatabase> pool = ODatabaseDocumentPool.global();
	
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
	public String getDBUserName() {
		return dbUserName;
	}

	@Override
	public String getDBUserPassword() {
		return dbUserPassword;
	}

	@Override
	public void setDBUserName(String userName) {
		this.dbUserName = userName;
	}

	@Override
	public void setDBUserPassword(String password) {
		this.dbUserPassword = password;
	}

	@Override
	public void setDatabaseThreadLocalFactory(
			ODatabaseThreadLocalFactory factory) {
		Orient.instance().registerThreadDatabaseFactory(factory);
	}

}
