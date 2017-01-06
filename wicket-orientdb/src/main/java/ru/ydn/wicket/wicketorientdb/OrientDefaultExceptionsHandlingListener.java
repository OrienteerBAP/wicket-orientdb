package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authorization.UnauthorizedActionException;
import org.apache.wicket.core.request.handler.IPageRequestHandler;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.lang.Exceptions;

import com.orientechnologies.orient.core.exception.OSchemaException;
import com.orientechnologies.orient.core.exception.OSecurityException;
import com.orientechnologies.orient.core.exception.OValidationException;

/**
 * Implementation of {@link IRequestCycleListener} for handling customly exceptions:
 * {@link OSecurityException}, {@link OValidationException}, {@link OSchemaException}, {@link IllegalStateException}
 */
public class OrientDefaultExceptionsHandlingListener extends
		AbstractRequestCycleListener {
	
	private static class UnauthorizedInstantiationHandler implements IRequestHandler {
		
		private Component component;
		
		public UnauthorizedInstantiationHandler(Component component) {
			this.component = component;
		}

		@Override
		public void respond(IRequestCycle requestCycle) {
			OrientDbWebApplication.get().onUnauthorizedInstantiation(component);
		}

		@Override
		public void detach(IRequestCycle requestCycle) {/*NOP*/}
		
	}
	
	@Override
	public IRequestHandler onException(RequestCycle cycle, Exception ex) {
		Throwable th = null;
		if((th=Exceptions.findCause(ex, OSecurityException.class))!=null
				|| (th=Exceptions.findCause(ex, OValidationException.class))!=null
				|| (th=Exceptions.findCause(ex, OSchemaException.class))!=null
				|| (th=Exceptions.findCause(ex, IllegalStateException.class))!=null && Exceptions.findCause(ex, WicketRuntimeException.class)==null)
		{
			Page page = extractCurrentPage(false);
			if(page==null) {
				return th instanceof OSecurityException ?
							new UnauthorizedInstantiationHandler(extractCurrentPage(true))
							:null; 
			}
			OrientDbWebSession.get().error(th.getMessage());
			return new RenderPageRequestHandler(new PageProvider(page),
			RenderPageRequestHandler.RedirectPolicy.ALWAYS_REDIRECT);
		}
		else if((th=Exceptions.findCause(ex, UnauthorizedActionException.class))!=null)
		{
			final UnauthorizedActionException unauthorizedActionException = (UnauthorizedActionException)th;
			return new UnauthorizedInstantiationHandler(unauthorizedActionException.getComponent());
		}
		else
		{
			return null;
		}
	}
	
	private Page extractCurrentPage(boolean fullSearch)
	{
		final RequestCycle requestCycle = RequestCycle.get();

		IRequestHandler handler = requestCycle.getActiveRequestHandler();

		if (handler == null)
		{
			handler = requestCycle.getRequestHandlerScheduledAfterCurrent();
			
			if(handler==null && fullSearch) {
				handler = OrientDbWebApplication.get().getRootRequestMapper().mapRequest(requestCycle.getRequest());
			}
		}

		if (handler instanceof IPageRequestHandler)
		{
			IPageRequestHandler pageRequestHandler = (IPageRequestHandler)handler;
			return (Page)pageRequestHandler.getPage();
		}
		return null;
	}

}
