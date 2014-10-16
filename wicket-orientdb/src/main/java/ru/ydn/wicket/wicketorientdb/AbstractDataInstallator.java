package ru.ydn.wicket.wicketorientdb;


import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

/**
 * Abstract class for installing data during application starting.
 */
public abstract class AbstractDataInstallator implements IApplicationListener
{
	private static final Logger log = LoggerFactory.getLogger(AbstractDataInstallator.class); 
	@Override
	public void onAfterInitialized(Application application) {
		OrientDbWebApplication app = (OrientDbWebApplication)application;
		ODatabaseRecord db = getDatabase(app);
		try
		{
			installData(app, db);
		}
		catch(Exception ex)
		{
			log.error("Data can't be installed", ex);
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
