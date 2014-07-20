package ru.ydn.wicket.wicketorientdb;

import java.util.Set;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.Request;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.exception.OSecurityAccessException;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.OUser;

public class OrientDbWebSession extends AuthenticatedWebSession {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private String username;
	private String password;
	private OUser user;
	private boolean userReloaded=false;
	
	public OrientDbWebSession(Request request) {
		super(request);
	}
	
	public static OrientDbWebSession get()
	{
		return (OrientDbWebSession)Session.get();
	}

	@Override
	public Roles getRoles() {
		Roles ret = new Roles();
		if(isSignedIn())
		{
			Set<ORole> roles = getUser().getRoles();
			for (ORole oRole : roles) {
				ret.add(oRole.getName());
				ORole parent = oRole.getParentRole();
				while(parent!=null && !ret.contains(parent.getName()))
				{
					ret.add(parent.getName());
					parent = parent.getParentRole();
				}
			}
		}
		return ret;
	}

	
	public ODatabaseRecord getDatabase()
	{
		return DefaultODatabaseThreadLocalFactory.castToODatabaseRecord(ODatabaseRecordThreadLocal.INSTANCE.get().getDatabaseOwner());
	}

	@Override
	public boolean authenticate(String username, String password) {
		try
		{
			ODatabaseRecord currentDB = getDatabase();
			IOrientDbSettings settings = OrientDbWebApplication.get().getOrientDbSettings();
			ODatabaseRecord newDB = DefaultODatabaseThreadLocalFactory.castToODatabaseRecord(
								settings.getDatabasePool().acquire(settings.getDBUrl(), username, password));
			currentDB.commit();
			currentDB.close();
			ODatabaseRecordThreadLocal.INSTANCE.set(newDB);
			setUser(username, password);
			user = newDB.getMetadata().getSecurity().getUser(username);
			newDB.setUser(user);
			newDB.begin();
			return true;
		} catch (OSecurityAccessException e)
		{
			return false;
		}
	}
	
	protected void setUser(String username, String password)
	{
		this.username = username;
		this.password = password;
		this.user = null;
	}
	
	public OUser getUser()
	{
		if(user!=null)
		{
			if(!userReloaded)
			{
				user.reload();
				userReloaded = true;
			}
		}
		else
		{
			user = getDatabase().getMetadata().getSecurity().getUser(username);
			userReloaded = true;
		}
		return user;
	}
	
	@Override
	public void detach() {
		super.detach();
		userReloaded = false;
	}
	
	String getUsername()
	{
		return username;
	}
	
	String getPassword()
	{
		return password;
	}
	
	@Override
	public void signOut() {
		super.signOut();
		this.username=null;
		this.password=null;
		this.user=null;
	}

}
