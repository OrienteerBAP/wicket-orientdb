package ru.ydn.wicket.wicketorientdb;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabasePoolBase;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;

public interface IOrientDbSettings {
	public String getDBUrl();
	public String getDefaultUserName();
	public String getDefaultUserPassword();
	public ODatabasePoolBase<? extends ODatabase> getDatabasePool();
	public ODatabaseThreadLocalFactory getDatabaseThreadLocalFactory();
	public void setDBUrl(String url);
	public void setDefaultUserName(String userName);
	public void setDefaultUserPassword(String password);
	public void setDatabasePool(ODatabasePoolBase<? extends ODatabase> pool);
	public void setDatabaseThreadLocalFactory(ODatabaseThreadLocalFactory factory);
}
