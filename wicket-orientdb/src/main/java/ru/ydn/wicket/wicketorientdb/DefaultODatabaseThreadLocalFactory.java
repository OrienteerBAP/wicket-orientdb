package ru.ydn.wicket.wicketorientdb;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseComplex;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;

public class DefaultODatabaseThreadLocalFactory implements ODatabaseThreadLocalFactory
{
	private OrientDbWebApplication app;
	
	public DefaultODatabaseThreadLocalFactory(OrientDbWebApplication app)
	{
		this.app = app;
	}
	
	@Override
	@SuppressWarnings({ "resource", "rawtypes" })
	public ODatabaseRecord getThreadDatabase() {
		IOrientDbSettings settings = app.getOrientDbSettings();
		ODatabase db = settings.getDatabasePool().acquire(settings.getDBUrl(), settings.getDBUserName(), settings.getDBUserPassword());
		while(db!=null && !(db instanceof ODatabaseRecord))
		{
			if(db instanceof ODatabaseComplex)
			{
				db = ((ODatabaseComplex)db).getUnderlying();
			}
		}
		return (ODatabaseRecord)db;
	}
}