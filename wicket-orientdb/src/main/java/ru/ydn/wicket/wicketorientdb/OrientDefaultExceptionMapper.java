package ru.ydn.wicket.wicketorientdb;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.DefaultExceptionMapper;
import org.apache.wicket.Page;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authorization.UnauthorizedActionException;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.util.lang.Exceptions;

import com.orientechnologies.orient.core.exception.OSchemaException;
import com.orientechnologies.orient.core.exception.OSecurityException;
import com.orientechnologies.orient.core.exception.OValidationException;

/**
 * Extension of {@link DefaultExceptionMapper} for handling customly exceptions:
 * {@link OSecurityException}, {@link OValidationException}, {@link OSchemaException}, {@link IllegalStateException}
 */
public class OrientDefaultExceptionMapper extends DefaultExceptionMapper {
	
	@Override
	protected IRequestHandler mapExpectedExceptions(Exception ex, Application application) {
		Throwable th = null;
		if((th=Exceptions.findCause(ex, OSecurityException.class))!=null
				|| (th=Exceptions.findCause(ex, OValidationException.class))!=null
				|| (th=Exceptions.findCause(ex, OSchemaException.class))!=null
				|| (th=Exceptions.findCause(ex, IllegalStateException.class))!=null && Exceptions.findCause(ex, WicketRuntimeException.class)==null)
		{
			Page page = extractCurrentPage();
			if(page==null) {
				if(th instanceof OSecurityException) 
					return (rh) -> OrientDbWebApplication.get().restartResponseAtSignInPage();
			}
			OrientDbWebSession.get().error(th.getMessage());
			return new RenderPageRequestHandler(new PageProvider(page),
									RenderPageRequestHandler.RedirectPolicy.ALWAYS_REDIRECT);
		}
		else if((th=Exceptions.findCause(ex, UnauthorizedActionException.class))!=null)
		{
			final Component component = ((UnauthorizedActionException)th).getComponent();
			return (rc) -> OrientDbWebApplication.get().onUnauthorizedInstantiation(component);
		}
		else
		{
			return super.mapExpectedExceptions(ex, application);
		}
	}
	
}
