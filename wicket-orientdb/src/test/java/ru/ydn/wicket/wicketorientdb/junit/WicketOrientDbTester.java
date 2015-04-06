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
package ru.ydn.wicket.wicketorientdb.junit;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.mock.MockHttpServletRequest;
import org.apache.wicket.protocol.http.mock.MockHttpServletResponse;
import org.apache.wicket.request.Url;
import org.apache.wicket.util.crypt.Base64;
import org.apache.wicket.util.tester.WicketTester;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.OMetadata;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

import ru.ydn.wicket.wicketorientdb.LazyAuthorizationRequestCycleListener;
import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

public class WicketOrientDbTester extends WicketTester {

    public WicketOrientDbTester(OrientDbWebApplication application) {
        super(application);
    }

    @Override
    public OrientDbWebApplication getApplication() {
        return (OrientDbWebApplication) super.getApplication();
    }

    @Override
    public OrientDbWebSession getSession() {
        return (OrientDbWebSession) super.getSession();
    }

    public ODatabaseDocument getDatabase() {
        return getSession().getDatabase();
    }

    public OMetadata getMetadata() {
        return getDatabase().getMetadata();
    }

    public OSchema getSchema() {
        return getMetadata().getSchema();
    }

    public boolean signIn(String username, String password) {
        return getSession().signIn(username, password);
    }

    public void signOut() {
        getSession().signOut();
    }

    public boolean isSignedIn() {
        return getSession().isSignedIn();
    }

    public String executeUrl(String url, final String method, final String content) throws Exception {
        return executeUrl(url, method, content, null, null);
    }

    public String executeUrl(String url, final String method, final String content, String username, String password) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest(getApplication(), getHttpSession(), getServletContext()) {
            {
                setMethod(method);
            }

            @Override
            public ServletInputStream getInputStream() throws IOException {
                if (content == null) {
                    return super.getInputStream();
                } else {
                    final StringReader sr = new StringReader(content);
                    return new ServletInputStream() {
                        @Override
                        public int read() throws IOException {
                            return sr.read();
                        }
                    };
                }
            }
        };

        Url url0 = Url.parse(url, Charset.forName(request.getCharacterEncoding()));
        request.setUrl(url0);
        request.setMethod(method);
        if (username != null && password != null) {
            request.setHeader(LazyAuthorizationRequestCycleListener.AUTHORIZATION_HEADER, "Basic " + Base64.encodeBase64String((username + ":" + password).getBytes()));
        }
        if (!processRequest(request)) {
            throw new IOException("Request was not sucessfully sent");
        }
        MockHttpServletResponse response = getLastResponse();
        int status = response.getStatus();
        if (status >= HttpServletResponse.SC_OK + 100) {
            throw new IOException("Code: " + response.getStatus() + " Message: " + response.getErrorMessage() + " Content: " + response.getDocument());
        } else {
            return response.getDocument();
        }
    }
}
