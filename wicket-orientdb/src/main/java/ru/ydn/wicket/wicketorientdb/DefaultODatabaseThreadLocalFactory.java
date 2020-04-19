package ru.ydn.wicket.wicketorientdb;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link ODatabaseThreadLocalFactory} for obtaining {@link ODatabaseDocument} according to {@link IOrientDbSettings}
 */
public class DefaultODatabaseThreadLocalFactory implements ODatabaseThreadLocalFactory
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultODatabaseThreadLocalFactory.class);

	private OrientDbWebApplication app;
	
	public DefaultODatabaseThreadLocalFactory(OrientDbWebApplication app)
	{
		this.app = app;
	}
	
	@Override
	public ODatabaseDocumentInternal getThreadDatabase() {
		IOrientDbSettings settings = app.getOrientDbSettings();
		OrientDbWebSession session = OrientDbWebSession.exists()?OrientDbWebSession.get():null;
		String username;
		String password;
		if(session!=null && session.isSignedIn())
		{
			username = session.getUsername();
			password = session.getPassword();
		}
		else
		{
			username = settings.getGuestUserName();
			password = settings.getGuestPassword();
		}
		return (ODatabaseDocumentInternal) settings.getContext().cachedPool(settings.getDbName(), username, password).acquire();
	}
}