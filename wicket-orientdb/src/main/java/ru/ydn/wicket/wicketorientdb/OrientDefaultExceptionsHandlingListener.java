package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.Page;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.core.request.handler.IPageRequestHandler;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.lang.Exceptions;

import com.orientechnologies.orient.core.exception.OSecurityException;
import com.orientechnologies.orient.core.exception.OValidationException;

public class OrientDefaultExceptionsHandlingListener extends
		AbstractRequestCycleListener {

	@Override
	public IRequestHandler onException(RequestCycle cycle, Exception ex) {
		Throwable th = null;
		if((th=Exceptions.findCause(ex, OSecurityException.class))!=null
				|| (th=Exceptions.findCause(ex, OValidationException.class))!=null
				|| (th=Exceptions.findCause(ex, IllegalStateException.class))!=null && Exceptions.findCause(ex, WicketRuntimeException.class)==null)
		{
			OrientDbWebSession.get().error(th.getMessage());
			return new RenderPageRequestHandler(new PageProvider(extractCurrentPage()),
			RenderPageRequestHandler.RedirectPolicy.ALWAYS_REDIRECT);
		}
		else
		{
			return null;
		}
	}
	
	private Page extractCurrentPage()
	{
		final RequestCycle requestCycle = RequestCycle.get();

		IRequestHandler handler = requestCycle.getActiveRequestHandler();

		if (handler == null)
		{
			handler = requestCycle.getRequestHandlerScheduledAfterCurrent();
		}

		if (handler instanceof IPageRequestHandler)
		{
			IPageRequestHandler pageRequestHandler = (IPageRequestHandler)handler;
			return (Page)pageRequestHandler.getPage();
		}
		return null;
	}

}
