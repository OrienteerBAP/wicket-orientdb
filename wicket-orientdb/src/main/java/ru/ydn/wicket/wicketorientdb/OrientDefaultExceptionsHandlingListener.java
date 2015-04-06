/**
 * Copyright (C) 2015 Ilia Naryzhny (phantom@ydn.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.ydn.wicket.wicketorientdb;

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
 * Implementation of {@link IRequestCycleListener} for handling customly
 * exceptions:
 * {@link OSecurityException}, {@link OValidationException}, {@link OSchemaException}, {@link IllegalStateException}
 */
public class OrientDefaultExceptionsHandlingListener extends
        AbstractRequestCycleListener {

    @Override
    public IRequestHandler onException(RequestCycle cycle, Exception ex) {
        Throwable th = null;
        if ((th = Exceptions.findCause(ex, OSecurityException.class)) != null
                || (th = Exceptions.findCause(ex, OValidationException.class)) != null
                || (th = Exceptions.findCause(ex, OSchemaException.class)) != null
                || (th = Exceptions.findCause(ex, IllegalStateException.class)) != null && Exceptions.findCause(ex, WicketRuntimeException.class) == null) {
            Page page = extractCurrentPage();
            if (page == null) {
                return null;
            }
            OrientDbWebSession.get().error(th.getMessage());
            return new RenderPageRequestHandler(new PageProvider(page),
                    RenderPageRequestHandler.RedirectPolicy.ALWAYS_REDIRECT);
        } else if ((th = Exceptions.findCause(ex, UnauthorizedActionException.class)) != null) {
            final UnauthorizedActionException unauthorizedActionException = (UnauthorizedActionException) th;
            return new IRequestHandler() {
                @Override
                public void respond(IRequestCycle requestCycle) {
                    OrientDbWebApplication.get().onUnauthorizedInstantiation(unauthorizedActionException.getComponent());
                }

                @Override
                public void detach(IRequestCycle requestCycle) {
                }
            };
        } else {
            return null;
        }
    }

    private Page extractCurrentPage() {
        final RequestCycle requestCycle = RequestCycle.get();

        IRequestHandler handler = requestCycle.getActiveRequestHandler();

        if (handler == null) {
            handler = requestCycle.getRequestHandlerScheduledAfterCurrent();
        }

        if (handler instanceof IPageRequestHandler) {
            IPageRequestHandler pageRequestHandler = (IPageRequestHandler) handler;
            return (Page) pageRequestHandler.getPage();
        }
        return null;
    }

}
