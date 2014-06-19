package ru.ydn.wicket.wicketorientdb;

import java.util.Set;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseComplex;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.exception.OSecurityAccessException;
import com.orientechnologies.orient.core.metadata.security.ORole;
import com.orientechnologies.orient.core.metadata.security.OUser;

public class OrientDbWebSession<K extends ODatabaseComplex> extends AuthenticatedWebSession {

	private OUser user;
	
	public OrientDbWebSession(Request request) {
		super(request);
	}
	
	public static OrientDbWebSession<ODatabaseComplex> get()
	{
		return (OrientDbWebSession<ODatabaseComplex>)Session.get();
	}

	@Override
	public Roles getRoles() {
		Roles ret = new Roles();
		if(isSignedIn())
		{
			Set<ORole> roles = user.getRoles();
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

	
	@SuppressWarnings("unchecked")
	public K getDatabase()
	{
		return (K)ODatabaseRecordThreadLocal.INSTANCE.get();
	}

	@Override
	public boolean authenticate(String username, String password) {
		try
		{
			OUser user = getDatabase().getMetadata().getSecurity().authenticate(username, password);
			setUser(user);
			return true;
		} catch (OSecurityAccessException e)
		{
			return false;
		}
	}
	
	protected void setUser(OUser user)
	{
		this.user = user;
	}
	
	public OUser getUser()
	{
		return user;
	}

	@Override
	public void signOut() {
		super.signOut();
		this.user=null;
	}
	
	

}
