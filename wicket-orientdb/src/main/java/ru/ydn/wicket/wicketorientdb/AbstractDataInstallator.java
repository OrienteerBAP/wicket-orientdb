package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

public abstract class AbstractDataInstallator implements IApplicationListener
{
	@Override
	public void onAfterInitialized(Application application) {
		OrientDbWebApplication app = (OrientDbWebApplication)application;
		IOrientDbSettings settings = app.getOrientDbSettings();
		String username = settings.getDBInstallatorUserName();
		String password = settings.getDBInstallatorUserPassword();
		ODatabaseRecord db = DefaultODatabaseThreadLocalFactory.castToODatabaseRecord(settings.getDatabasePool().acquire(settings.getDBUrl(), username, password));
		db.begin();
		try
		{
			installData(db);
			db.commit();
		}
		catch(Exception ex)
		{
			db.rollback();
		}
		finally
		{
			db.close();
		}
	}
	
	protected abstract void installData(ODatabaseRecord db);

	@Override
	public void onBeforeDestroyed(Application application) {
		
	}

}
