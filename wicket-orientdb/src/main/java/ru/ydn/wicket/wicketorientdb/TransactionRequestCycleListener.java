package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.metadata.security.OUser;

/**
 * Implementation of {@link IRequestCycleListener} for starting and stoping transactions just for pages and dynamic resources
 */
public class TransactionRequestCycleListener extends
		AbstractContentAwareTransactionRequestCycleListener {
	@Override
	public void start(RequestCycle cycle) {
		OrientDbWebSession session = OrientDbWebSession.get();
		ODatabaseDocument db = session.getDatabase();
		//It's required to have ability to check security rights locally
		OSecurityUser oUser = db.getUser();
		if(oUser.getDocument()!=null && oUser.getDocument().getIdentity()!=null && !oUser.getDocument().getIdentity().isValid())
		{
			db.setUser(db.getMetadata().getSecurity().getUser(oUser.getName()));
		}
		db.begin();
	}
	

	@Override
	public void end(RequestCycle cycle) {
		ODatabaseRecordThreadLocal.INSTANCE.get().commit();
	}
	
	@Override
	public void onDetach(RequestCycle cycle) {
		ODatabaseRecordThreadLocal.INSTANCE.get().close();
		ODatabaseRecordThreadLocal.INSTANCE.remove();
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
