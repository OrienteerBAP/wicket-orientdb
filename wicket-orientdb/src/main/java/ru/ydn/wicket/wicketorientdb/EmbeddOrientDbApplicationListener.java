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

import java.io.File;
import java.net.URL;

import org.apache.wicket.Application;
import org.apache.wicket.IApplicationListener;
import org.apache.wicket.WicketRuntimeException;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import com.orientechnologies.orient.server.config.OServerConfiguration;

/**
 * Implementation of {@link IApplicationListener} to run OrientDB in embedded
 * mode
 */
public class EmbeddOrientDbApplicationListener implements IApplicationListener {

    private URL url;
    private File configFile;
    private String config;
    protected OServerConfiguration serverConfiguration;

    public EmbeddOrientDbApplicationListener() {

    }

    public EmbeddOrientDbApplicationListener(URL url) {
        this.url = url;
    }

    public EmbeddOrientDbApplicationListener(File configFile) {
        this.configFile = configFile;
    }

    public EmbeddOrientDbApplicationListener(String config) {
        this.config = config;
    }

    public EmbeddOrientDbApplicationListener(OServerConfiguration serverConfiguration) {
        this.serverConfiguration = serverConfiguration;
    }

    @Override
    public void onAfterInitialized(Application application) {
        try {
            OrientDbWebApplication app = (OrientDbWebApplication) application;
            OServer server = OServerMain.create(false);
            if (url != null) {
                server.startup(url.openStream());
            } else if (configFile != null) {
                server.startup(configFile);
            } else if (config != null) {
                server.startup(config);
            } else if (serverConfiguration != null) {
                server.startup(serverConfiguration);
            } else {
                server.startup();
            }
            server.activate();
            server.removeShutdownHook();
            app.setServer(server);
            app.getOrientDbSettings().setDatabasePoolFactory(server.getDatabasePoolFactory());
            onAfterServerStartupAndActivation(app);
        } catch (Exception e) {
            throw new WicketRuntimeException("Can't start OrientDB Embedded Server", e);
        }
    }

    public void onAfterServerStartupAndActivation(OrientDbWebApplication app) throws Exception {

    }

    @Override
    public void onBeforeDestroyed(Application application) {
        OrientDbWebApplication app = (OrientDbWebApplication) application;
        OServer server = app.getServer();
        if (server != null) {
            server.shutdown();
        }
    }

}
