package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.lang.Objects;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;

/**
 * Implementation of {@link IRequestCycleListener} for starting and stoping transactions just for pages and dynamic resources
 */
public class TransactionRequestCycleListener extends
		AbstractContentAwareTransactionRequestCycleListener {
	@Override
	public void start(RequestCycle cycle) {
		OrientDbWebSession session = OrientDbWebSession.get();
		ODatabaseDocumentInternal db = session.getDatabase();
		//It's required to have ability to check security rights locally
		OSecurityUser oUser = session.getUser();
		OSecurityUser dbUser = db.getUser();
		if(oUser!=null && oUser.getDocument()!=null 
				&& oUser.getDocument().getIdentity()!=null 
				&& (!oUser.getDocument().getIdentity().isValid() || dbUser==null || !Objects.equal(dbUser.getName(), oUser.getName())))
		{
			db.setUser(db.getMetadata().getSecurity().getUser(oUser.getName()));
		}
		db.begin();
	}
	

	@Override
	public void end(RequestCycle cycle) {
		ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().getIfDefined();
		if(db!=null && db.getTransaction().isActive()) db.commit();
	}
	
	@Override
	public void onDetach(RequestCycle cycle) {
		ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().getIfDefined();
		if(db!=null) {
			if(db.getTransaction().isActive()) db.commit(true);
			db.close();
			ODatabaseRecordThreadLocal.instance().remove();
		}
	}
	
	

	@Override
	public IRequestHandler onException(RequestCycle cycle, Exception ex) {
		ODatabaseDocument db = ODatabaseRecordThreadLocal.instance().getIfDefined();
		if(db!=null && !db.isClosed()) db.rollback();
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
