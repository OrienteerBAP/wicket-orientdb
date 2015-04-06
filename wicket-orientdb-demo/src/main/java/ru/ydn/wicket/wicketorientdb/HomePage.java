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

import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.devutils.debugbar.DebugBar;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class HomePage extends WebPage {

    private static final long serialVersionUID = 1L;

    public HomePage(final PageParameters parameters) {
        super(parameters);

        add(new DebugBar("debugBar"));
        add(new Label("dbName", new PropertyModel<String>(this, "session.database.name")));
        add(new Label("dbUrl", new PropertyModel<String>(this, "session.database.URL")));
        add(new Label("dbUserName", new PropertyModel<String>(this, "session.database.user.name")));
        add(new Label("signedIn", new PropertyModel<String>(this, "session.signedIn")));
        add(new Label("signedInUser", new PropertyModel<String>(this, "session.user.name")));
//        ((OrientDbWebSession)getSession()).getDatabase().getUser().
        add(new SignInPanel("signInPanel"));
    }
}
