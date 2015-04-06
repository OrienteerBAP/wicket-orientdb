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
import org.apache.wicket.IApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;

/**
 * Abstract class for installing data during application starting.
 */
public abstract class AbstractDataInstallator implements IApplicationListener {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDataInstallator.class);

    @Override
    public void onAfterInitialized(Application application) {
        OrientDbWebApplication app = (OrientDbWebApplication) application;
        ODatabaseDocument db = getDatabase(app);
        try {
            installData(app, db);
        } catch (Exception ex) {
            LOG.error("Data can't be installed", ex);
        } finally {
            db.close();
        }
    }

    protected ODatabaseDocument getDatabase(OrientDbWebApplication app) {
        IOrientDbSettings settings = app.getOrientDbSettings();
        String username = settings.getDBInstallatorUserName();
        String password = settings.getDBInstallatorUserPassword();
        return settings.getDatabasePoolFactory().get(settings.getDBUrl(), username, password).acquire();
    }

    protected abstract void installData(OrientDbWebApplication app, ODatabaseDocument db);

    @Override
    public void onBeforeDestroyed(Application application) {

    }

}
