package ru.ydn.wicket.wicketorientdb;

import java.util.Set;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OSecurityAccessException;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.security.OSecurityRole;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.model.ODocumentModel;

/**
 * Implementation of {@link WebSession} which shold be used in OrientDB based applications
 */
public class OrientDbWebSession extends AuthenticatedWebSession {

	private static final long serialVersionUID = 2L;
	private String username;
	private String password;
	private IModel<ODocument> userModel = new ODocumentModel();
	
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
			Set<? extends OSecurityRole> roles = getUser().getRoles();
			for (OSecurityRole oRole : roles) {
				ret.add(oRole.getName());
				OSecurityRole parent = oRole.getParentRole();
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
	 * @return {@link ODatabaseDocument} for current request
	 */
	public ODatabaseDocumentInternal getDatabase()
	{
		return ODatabaseRecordThreadLocal.instance().get();
	}
	
	/**
	 * @return {@link OSchema} for current request
	 */
	public OSchema getSchema() {
		return getDatabase().getMetadata().getSchema();
	}

	@Override
	public boolean authenticate(String username, String password) {
		ODatabaseDocument currentDB = getDatabase();
		try {
			boolean inTransaction = currentDB.getTransaction().isActive();
			IOrientDbSettings settings = OrientDbWebApplication.get().getOrientDbSettings();
			ODatabaseSession newDB = settings.getContext().cachedPool(settings.getDbName(), username, password).acquire();
			if (newDB != currentDB) {
				currentDB.activateOnCurrentThread();
				currentDB.commit();
				currentDB.close();
				newDB.activateOnCurrentThread();
			}
			setUser(username, password);
			userModel.setObject(newDB.getUser().getDocument());
			if (inTransaction && !newDB.getTransaction().isActive()) {
				newDB.begin();
			}
			return true;
		} catch (OSecurityAccessException e) {
			currentDB.activateOnCurrentThread();
			return false;
		}
	}
	
	protected void setUser(String username, String password)
	{
		this.username = username;
		this.password = password;
		this.userModel.setObject(null);
	}
	
	public OSecurityUser getEffectiveUser()
	{
		OSecurityUser ret = getUser();
		return ret!=null?ret:getDatabase().getUser();
	}
	
	
	
	/**
	 * @return currently signed in {@link OUser}. Returns null in case of no user was signed in.
	 */
	public OSecurityUser getUser()
	{
		ODocument userDoc = getUserAsODocument();
		return userDoc!=null?new OUser(userDoc):null;
	}
	
	/**
	 * @return {@link ODocument} for a logged in user
	 */
	public ODocument getUserAsODocument()
	{
		return userModel.getObject();
	}
	
	@Override
	public void detach() {
		super.detach();
		userModel.detach();
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
		this.userModel.setObject(null);
		ODatabaseRecordThreadLocal.instance().remove();
	}

}
