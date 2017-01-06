package ru.ydn.wicket.wicketorientdb;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;

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
	public ODatabaseDocumentInternal getThreadDatabase() {
		IOrientDbSettings settings = app.getOrientDbSettings();
		OrientDbWebSession session = OrientDbWebSession.get();
		ODatabaseDocumentInternal db;
		String username;
		String password;
		if(session.isSignedIn())
		{
			username = session.getUsername();
			password = session.getPassword();
		}
		else
		{
			username = settings.getGuestUserName();
			password = settings.getGuestPassword();
		}
		db = settings.getDatabasePoolFactory().get(settings.getDBUrl(), username, password).acquire();
		return db;
	}
	
	/**
	 * Utility method to obtain {@link ODatabaseDocument} from {@link ODatabase}
	 * @param db {@link ODatabase} to cast from
	 * @return {@link ODatabaseDocument} for a specified {@link ODatabase}
	 */
	public static ODatabaseDocument castToODatabaseDocument(ODatabase<?> db)
	{
		while(db!=null && !(db instanceof ODatabaseDocument))
		{
			if(db instanceof ODatabaseInternal<?>)
			{
				db = ((ODatabaseInternal<?>)db).getUnderlying();
			}
		}
		return (ODatabaseDocument)db;
	}
}