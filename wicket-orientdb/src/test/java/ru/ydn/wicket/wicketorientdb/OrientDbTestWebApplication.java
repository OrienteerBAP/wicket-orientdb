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

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.markup.html.WebPage;

import ru.ydn.wicket.wicketorientdb.rest.OrientDBHttpAPIResource;
import ru.ydn.wicket.wicketorientdb.web.OrientDbTestPage;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

public class OrientDbTestWebApplication extends OrientDbWebApplication {

    public static final String DB_NAME = "WicketOrientTestDb";
    public static final String DB_MEMORY_URL = "memory:" + DB_NAME;
//    public static final String DB_REMOTE_URL = "remote:localhost/"+DB_NAME;

    @Override
    public void init() {
        super.init();
        getApplicationListeners().add(new EmbeddOrientDbApplicationListener(OrientDbTestWebApplication.class.getResource("db.config.xml")) {

            @Override
            public void onAfterServerStartupAndActivation(OrientDbWebApplication app) throws Exception {
                IOrientDbSettings settings = app.getOrientDbSettings();
                ODatabaseDocumentTx db = new ODatabaseDocumentTx(DB_MEMORY_URL);
                if (!db.exists()) {
                    db = db.create();
                }
                if (db.isClosed()) {
                    db.open(settings.getDBInstallatorUserName(), settings.getDBInstallatorUserPassword());
                }
                db.getMetadata().load();
                db.close();
            }

            /*@Override
             public void onBeforeDestroyed(Application application) {
             super.onBeforeDestroyed(application);
             //TODO: remove this after fixing bug orientdb Bug#3387
             try{Thread.sleep(100);} catch (InterruptedException e){}
             }*/
        });
        getRequestCycleListeners().add(new LazyAuthorizationRequestCycleListener());
        getOrientDbSettings().setDBUrl(DB_MEMORY_URL);
        getOrientDbSettings().setDBUserName("admin");
        getOrientDbSettings().setDBUserPassword("admin");
        getOrientDbSettings().setDBInstallatorUserName("admin");
        getOrientDbSettings().setDBInstallatorUserPassword("admin");
        getApplicationListeners().add(new TestDataInstallator());
        mountOrientDbRestApi();
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return SignInPage.class;
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return OrientDbTestPage.class;
    }

}
