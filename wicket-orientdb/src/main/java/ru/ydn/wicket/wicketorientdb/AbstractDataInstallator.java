package ru.ydn.wicket.wicketorientdb;


import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;

/**
 * Abstract class for installing data during application starting.
 */
public abstract class AbstractDataInstallator implements IApplicationListener
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractDataInstallator.class); 
	@Override
	public void onAfterInitialized(Application application) {
		OrientDbWebApplication app = (OrientDbWebApplication)application;
		ODatabaseDocument db = getDatabase(app);
		try
		{
			installData(app, db);
		}
		catch(Exception ex)
		{
			LOG.error("Data can't be installed", ex);
		}
		finally
		{
			db.close();
		}
	}
	
	protected ODatabaseDocument getDatabase(OrientDbWebApplication app)
	{
		IOrientDbSettings settings = app.getOrientDbSettings();
		String username = settings.getDBInstallatorUserName();
		String password = settings.getDBInstallatorUserPassword();
		return settings.getDatabasePoolFactory().get(settings.getDBUrl(), username, password).acquire();
	}
	
	protected abstract void installData(OrientDbWebApplication app, ODatabaseDocument db);

	@Override
	public void onBeforeDestroyed(Application application) {
		
	}

}
