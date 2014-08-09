package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

public abstract class AbstractDataInstallator implements IApplicationListener
{
	@Override
	public void onAfterInitialized(Application application) {
		OrientDbWebApplication app = (OrientDbWebApplication)application;
		ODatabaseRecord db = getDatabase(app);
		db.begin();
		try
		{
			installData(app, db);
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
	
	protected ODatabaseRecord getDatabase(OrientDbWebApplication app)
	{
		IOrientDbSettings settings = app.getOrientDbSettings();
		String username = settings.getDBInstallatorUserName();
		String password = settings.getDBInstallatorUserPassword();
		return DefaultODatabaseThreadLocalFactory.castToODatabaseRecord(settings.getDatabasePool().acquire(settings.getDBUrl(), username, password));
	}
	
	protected abstract void installData(OrientDbWebApplication app, ODatabaseRecord db);

	@Override
	public void onBeforeDestroyed(Application application) {
		
	}

}
