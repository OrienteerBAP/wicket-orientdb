package ru.ydn.wicket.wicketorientdb;

import java.util.List;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabasePoolBase;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;
import com.orientechnologies.orient.core.hook.ORecordHook;

public interface IOrientDbSettings {
	public String getDBUrl();
	public String getDBUserName();
	public String getDBUserPassword();
	public String getDBInstallatorUserName();
	public String getDBInstallatorUserPassword();
	public ODatabasePoolBase<? extends ODatabase> getDatabasePool();
	public ODatabaseThreadLocalFactory getDatabaseThreadLocalFactory();
	public void setDBUrl(String url);
	public void setDBUserName(String userName);
	public void setDBUserPassword(String password);
	public void setDBInstallatorUserName(String userName);
	public void setDBInstallatorUserPassword(String password);
	public void setDatabasePool(ODatabasePoolBase<? extends ODatabase> pool);
	public void setDatabaseThreadLocalFactory(ODatabaseThreadLocalFactory factory);
	public List<ORecordHook> getORecordHooks();
}
