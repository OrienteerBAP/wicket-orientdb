package ru.ydn.wicket.wicketorientdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseComplex;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

public class DefaultODatabaseThreadLocalFactory implements ODatabaseThreadLocalFactory
{
	private static final Logger log = LoggerFactory.getLogger(DefaultODatabaseThreadLocalFactory.class);
	private OrientDbWebApplication app;
	
	public DefaultODatabaseThreadLocalFactory(OrientDbWebApplication app)
	{
		this.app = app;
	}
	
	@Override
	@SuppressWarnings({ "resource", "rawtypes" })
	public ODatabaseRecord getThreadDatabase() {
		IOrientDbSettings settings = app.getOrientDbSettings();
		OrientDbWebSession session = OrientDbWebSession.get();
		ODatabaseRecord db;
		String username;
		String password;
		/*if(session.isSignedIn() && !session.isUserValid())
		{
			session.invalidateNow();
		}*/
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
		log.info("Logging in with username {} and password {}", username, password);
		db = castToODatabaseRecord(settings.getDatabasePool().acquire(settings.getDBUrl(), username, password));
		log.info("Logginin is OK");
		
		return db;
	}
	
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