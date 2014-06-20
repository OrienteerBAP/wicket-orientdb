package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import com.orientechnologies.orient.core.metadata.security.OUser;

public class TransactionRequestCycleListener extends
		AbstractContentAwareTransactionRequestCycleListener {

	@Override
	public void start(RequestCycle cycle) {
		OrientDbWebSession session = OrientDbWebSession.get();
		if(session.isSignedIn())
		{
			OUser user = session.getUser();
			user.reload();
			session.getDatabase().setUser(user);
		}
		session.getDatabase().begin();
	}

	@Override
	public void end(RequestCycle cycle) {
		OrientDbWebSession session = OrientDbWebSession.get();
		session.getDatabase().commit();
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
