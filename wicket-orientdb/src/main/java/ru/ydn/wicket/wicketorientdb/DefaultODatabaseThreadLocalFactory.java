package ru.ydn.wicket.wicketorientdb;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseComplex;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

/**
 * Implemenetation of {@link ODatabaseThreadLocalFactory} for obtaining {@link ODatabaseRecord} according to {@link IOrientDbSettings}
 */
public class DefaultODatabaseThreadLocalFactory implements ODatabaseThreadLocalFactory
{
	private OrientDbWebApplication app;
	
	public DefaultODatabaseThreadLocalFactory(OrientDbWebApplication app)
	{
		this.app = app;
	}
	
	@Override
	public ODatabaseRecord getThreadDatabase() {
		IOrientDbSettings settings = app.getOrientDbSettings();
		OrientDbWebSession session = OrientDbWebSession.get();
		ODatabaseRecord db;
		String username;
		String password;
		if(session.isSignedIn())
		{
			username = session.getUsername();
			password = session.getPassword();
		}
		else
		{
			username = settings.getDBUserName();
			password = settings.getDBUserPassword();
		}
		db = castToODatabaseRecord(settings.getDatabasePool().acquire(settings.getDBUrl(), username, password));
		return db;
	}
	
	/**
	 * Utility method to obtain {@link ODatabaseRecord} from {@link ODatabase}
	 * @param db
	 * @return
	 */
	public static ODatabaseRecord castToODatabaseRecord(ODatabase db)
	{
		while(db!=null && !(db instanceof ODatabaseRecord))
		{
			if(db instanceof ODatabaseComplex)
			{
				db = ((ODatabaseComplex<?>)db).getUnderlying();
			}
		}
		return (ODatabaseRecord)db;
	}
}