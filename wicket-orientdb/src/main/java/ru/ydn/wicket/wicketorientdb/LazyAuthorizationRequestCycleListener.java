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

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.authorization.AuthorizationException;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.util.crypt.Base64;

public class LazyAuthorizationRequestCycleListener extends
        AbstractRequestCycleListener {

    public static final MetaDataKey<Boolean> LAZY_AUTHORIZED = new MetaDataKey<Boolean>() {
    };
    public static final String AUTHORIZATION_HEADER = "Authorization";

    private static class LazyAuthorizationException extends AuthorizationException {

        public LazyAuthorizationException() {
            super("Deny: HTTP Basic Authorization");
        }

    }

    @Override
    public void onBeginRequest(RequestCycle cycle) {
        WebRequest request = (WebRequest) cycle.getRequest();
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization != null && authorization.startsWith("Basic")) {
            String[] pair = new String(Base64.decodeBase64(authorization.substring(6))).split(":");
            if (pair.length == 2) {
                String userName = pair[0];
                String password = pair[1];
                OrientDbWebSession session = OrientDbWebSession.get();
                if (!session.signIn(userName, password)) {
                    cycle.setMetaData(LAZY_AUTHORIZED, false);
                }
            }
        }
    }

    @Override
    public void onRequestHandlerResolved(RequestCycle cycle,
            IRequestHandler handler) {
        Boolean lazyAuthorized = cycle.getMetaData(LAZY_AUTHORIZED);
        if (lazyAuthorized != null && !lazyAuthorized) {
            cycle.setMetaData(LAZY_AUTHORIZED, null);
            throw new LazyAuthorizationException();
        }
    }

}
