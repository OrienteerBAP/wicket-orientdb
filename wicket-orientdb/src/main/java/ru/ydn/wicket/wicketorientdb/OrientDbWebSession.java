package ru.ydn.wicket.wicketorientdb;

import java.util.Set;

import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseComplex;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.metadata.security.ORole;

public class OrientDbWebSession<K extends ODatabaseComplex> extends AbstractAuthenticatedWebSession {

	public OrientDbWebSession(Request request) {
		super(request);
	}

	@Override
	public Roles getRoles() {
		Set<ORole> roles = getDatabase().getUser().getRoles();
		Roles ret = new Roles();
		for (ORole oRole : roles) {
			ret.add(oRole.getName());
			ORole parent = oRole.getParentRole();
			while(parent!=null && !ret.contains(parent.getName()))
			{
				ret.add(parent.getName());
				parent = parent.getParentRole();
			}
		}
		return ret;
	}

	@Override
	public boolean isSignedIn() {
		return false;
		//TODO
	}
	
	@SuppressWarnings("unchecked")
	public K getDatabase()
	{
		return (K)ODatabaseRecordThreadLocal.INSTANCE.get();
	}

}
