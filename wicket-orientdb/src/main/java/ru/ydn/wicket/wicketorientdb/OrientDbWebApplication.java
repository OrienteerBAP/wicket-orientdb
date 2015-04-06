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
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IApplicationListener;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.protocol.http.WebApplication;

import ru.ydn.wicket.wicketorientdb.converter.ODocumentConverter;
import ru.ydn.wicket.wicketorientdb.converter.OIdentifiableConverter;
import ru.ydn.wicket.wicketorientdb.rest.OrientDBHttpAPIResource;
import ru.ydn.wicket.wicketorientdb.security.WicketOrientDbAuthorizationStrategy;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.ODatabaseLifecycleListener;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.server.OServer;

/**
 * {@link WebApplication} realization for applications on top of OrientDB
 */
public abstract class OrientDbWebApplication extends AuthenticatedWebApplication {

    private IOrientDbSettings orientDbSettings = new OrientDbSettings();
    private OServer server;

    @Override
    protected Class<? extends OrientDbWebSession> getWebSessionClass() {
        return OrientDbWebSession.class;
    }

    /**
     * @return settings for the application
     */
    public IOrientDbSettings getOrientDbSettings() {
        return orientDbSettings;
    }

    /**
     * Explicit set of settings for the application. Doesn't recommended to use
     * this method. Consider to use getOrientDBSettings().setXXX()
     *
     * @param orientDbSettings
     */
    public void setOrientDbSettings(IOrientDbSettings orientDbSettings) {
        this.orientDbSettings = orientDbSettings;
    }

    public static OrientDbWebApplication get() {
        return (OrientDbWebApplication) WebApplication.get();
    }

    public static OrientDbWebApplication lookupApplication() {
        return lookupApplication(OrientDbWebApplication.class);
    }

    protected static <T extends OrientDbWebApplication> T lookupApplication(Class<T> appClass) {
        Application app = Application.get();
        if (app != null && appClass.isInstance(app)) {
            return (T) app;
        } else {
            for (String appKey : Application.getApplicationKeys()) {
                app = Application.get(appKey);
                if (appClass.isInstance(app)) {
                    return (T) app;
                }
            }
        }
        return null;
    }

    @Override
    protected void init() {
        super.init();
        Orient.instance().registerThreadDatabaseFactory(new DefaultODatabaseThreadLocalFactory(this));
        Orient.instance().addDbLifecycleListener(new ODatabaseLifecycleListener() {

            @Override
            public void onOpen(ODatabaseInternal iDatabase) {
                for (ORecordHook oRecordHook : getOrientDbSettings().getORecordHooks()) {
                    iDatabase.registerHook(oRecordHook);
                }
            }

            @Override
            public void onCreate(ODatabaseInternal iDatabase) {
                for (ORecordHook oRecordHook : getOrientDbSettings().getORecordHooks()) {
                    iDatabase.registerHook(oRecordHook);
                }
            }

            @Override
            public void onClose(ODatabaseInternal iDatabase) {
                for (ORecordHook oRecordHook : getOrientDbSettings().getORecordHooks()) {
                    iDatabase.unregisterHook(oRecordHook);
                }
            }

            public PRIORITY getPriority() {
                return PRIORITY.REGULAR;
            }

            @Override
            public void onCreateClass(ODatabaseInternal iDatabase, OClass iClass) {

            }

            @Override
            public void onDropClass(ODatabaseInternal iDatabase, OClass iClass) {

            }
        });
        getRequestCycleListeners().add(newTransactionRequestCycleListener());
        getRequestCycleListeners().add(new OrientDefaultExceptionsHandlingListener());
        getSecuritySettings().setAuthorizationStrategy(new WicketOrientDbAuthorizationStrategy(this));
        getApplicationListeners().add(new IApplicationListener() {

            @Override
            public void onAfterInitialized(Application application) {
                Orient.instance().startup();
                Orient.instance().removeShutdownHook();
            }

            @Override
            public void onBeforeDestroyed(Application application) {
                Orient.instance().shutdown();
            }
        });
    }

    protected TransactionRequestCycleListener newTransactionRequestCycleListener() {
        return new TransactionRequestCycleListener();
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator locator = new ConverterLocator();
        locator.set(OIdentifiable.class, new OIdentifiableConverter<OIdentifiable>());
        locator.set(ODocument.class, new ODocumentConverter());
        return locator;
    }

    public OServer getServer() {
        return server;
    }

    public void setServer(OServer server) {
        this.server = server;
    }

    protected void mountOrientDbRestApi() {
        OrientDBHttpAPIResource.mountOrientDbRestApi(this);
    }

}
