package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.metadata.security.OUser;

public class TransactionRequestCycleListener extends
		AbstractContentAwareTransactionRequestCycleListener {
	@Override
	public void start(RequestCycle cycle) {
		OrientDbWebSession session = OrientDbWebSession.get();
		ODatabaseRecord db = session.getDatabase();
		/*if(db.isClosed()) System.out.println("DataBase CLOSED");
		OUser oUser;
		if(session.isSignedIn())
		{
			oUser = session.getUser();
			oUser.reload();
		}
		else
		{
			IOrientDbSettings settings = OrientDbWebApplication.get().getOrientDbSettings();
			oUser = db.getMetadata().getSecurity().getUser(settings.getDefaultUserName());
			if(!oUser.checkPassword(settings.getDefaultUserPassword()))
			{
				throw new WicketRuntimeException("Incorrect password for default user was specified");
			}
		}
		db.setUser(oUser);*/
		//It's required to have ability to check security rights locally
		OUser oUser = db.getUser();
		if(oUser.getDocument()!=null && oUser.getDocument().getIdentity()!=null && !oUser.getDocument().getIdentity().isValid())
		{
			db.setUser(db.getMetadata().getSecurity().getUser(oUser.getName()));
		}
		db.begin();
	}
	

	@Override
	public void end(RequestCycle cycle) {
		OrientDbWebSession session = OrientDbWebSession.get();
		ODatabaseRecord db = session.getDatabase();
		try
		{
			session.getDatabase().commit();
		} 
		finally
		{
			//Following 3 lines are required to correctly close pooled resource: pool is using username as a key
			/*IOrientDbSettings settings = OrientDbWebApplication.get().getOrientDbSettings();
			OUser oUser = db.getMetadata().getSecurity().getUser(settings.getDBUserName());
			db.setUser(oUser);*/
			db.close();
			ODatabaseRecordThreadLocal.INSTANCE.remove();
		}
	}
	
	

	@Override
	public IRequestHandler onException(RequestCycle cycle, Exception ex) {
		OrientDbWebSession session = OrientDbWebSession.get();
		session.getDatabase().rollback();
		return null;
	}
	

	@Override
	public boolean isOurContent(RequestCycle cycle, IRequestHandler handler) {
		if(handler instanceof ResourceReferenceRequestHandler)
		{
			ResourceReferenceRequestHandler rrrHandler = (ResourceReferenceRequestHandler)handler;
			ResourceReference reference = rrrHandler.getResourceReference();
			return !(reference instanceof PackageResourceReference);
		}
		return true;
	}

}
