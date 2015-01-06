package ru.ydn.wicket.wicketorientdb.junit;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.OMetadata;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

public class WicketOrientDbTester extends WicketTester
{

	public WicketOrientDbTester(OrientDbWebApplication application)
	{
		super(application);
	}

	@Override
	public OrientDbWebApplication getApplication() {
		return (OrientDbWebApplication) super.getApplication();
	}
	
	@Override
	public OrientDbWebSession getSession()
	{
		return (OrientDbWebSession)super.getSession();
	}
	
	public ODatabaseDocument getDatabase()
	{
		return getSession().getDatabase();
	}
	
	public OMetadata getMetadata()
	{
		return getDatabase().getMetadata();
	}
	
	public OSchema getSchema()
	{
		return getMetadata().getSchema();
	}
	
	public boolean signIn(String username, String password)
	{
		return getSession().signIn(username, password);
	}
	
	public void signOut()
	{
		getSession().signOut();
	}
	
	public boolean isSignedIn()
	{
		return getSession().isSignedIn();
	}
}
