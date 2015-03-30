package ru.ydn.wicket.wicketorientdb;

import java.util.Set;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OSecurityAccessException;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.metadata.security.OUser;

/**
 * Implemetation of {@link WebSession} which shold be used in OrientDB based applications
 */
public class OrientDbWebSession extends AuthenticatedWebSession {

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

	/**
	 * @return {@link ODatabaseRecord} for current request
	 */
	public ODatabaseDocument getDatabase()
	{
		return DefaultODatabaseThreadLocalFactory.castToODatabaseDocument(ODatabaseRecordThreadLocal.INSTANCE.get().getDatabaseOwner());
	}

	@Override
	public boolean authenticate(String username, String password) {
		try
		{
			ODatabaseDocument currentDB = getDatabase();
			IOrientDbSettings settings = OrientDbWebApplication.get().getOrientDbSettings();
			ODatabaseDocument newDB = settings.getDatabasePoolFactory().get(settings.getDBUrl(), username, password).acquire();
			if(newDB!=currentDB)
			{
				currentDB.commit();
				currentDB.close();
			}
			ODatabaseRecordThreadLocal.INSTANCE.set((ODatabaseDocumentInternal)newDB);
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
	
	public OSecurityUser getEffectiveUser()
	{
		OUser ret = getUser();
		return ret!=null?ret:getDatabase().getUser();
	}
	
	/**
	 * @return currently signed in {@link OUser}. Returns null in case of no user was signed in.
	 */
	public OUser getUser()
	{
		if(user!=null)
		{
			if(!userReloaded)
			{
				user.load();
				userReloaded = true;
			}
		}
		else
		{
			user = username!=null?getDatabase().getMetadata().getSecurity().getUser(username):null;
			userReloaded = true;
		}
		return user;
	}
	
	@Override
	public void detach() {
		super.detach();
		userReloaded = false;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	@Override
	public void signOut() {
		super.signOut();
		this.username=null;
		this.password=null;
		this.user=null;
		ODatabaseRecordThreadLocal.INSTANCE.remove();
	}

}
