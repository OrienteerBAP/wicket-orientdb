package ru.ydn.wicket.wicketorientdb;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabasePoolBase;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;

public interface IOrientDbSettings {
	public String getDBUrl();
	public String getDBUserName();
	public String getDBUserPassword();
	public ODatabasePoolBase<? extends ODatabase> getDatabasePool();
	public ODatabaseThreadLocalFactory getDatabaseThreadLocalFactory();
	public void setDBUrl(String url);
	public void setDBUserName(String userName);
	public void setDBUserPassword(String password);
	public void setDatabasePool(ODatabasePoolBase<? extends ODatabase> pool);
	public void setDatabaseThreadLocalFactory(ODatabaseThreadLocalFactory factory);
}
