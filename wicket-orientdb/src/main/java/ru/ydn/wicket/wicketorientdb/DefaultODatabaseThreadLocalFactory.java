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

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseInternal;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;

/**
 * Implemenetation of {@link ODatabaseThreadLocalFactory} for obtaining
 * {@link ODatabaseRecord} according to {@link IOrientDbSettings}
 */
public class DefaultODatabaseThreadLocalFactory implements ODatabaseThreadLocalFactory {

    private OrientDbWebApplication app;

    public DefaultODatabaseThreadLocalFactory(OrientDbWebApplication app) {
        this.app = app;
    }

    @Override
    public ODatabaseDocumentInternal getThreadDatabase() {
        IOrientDbSettings settings = app.getOrientDbSettings();
        OrientDbWebSession session = OrientDbWebSession.get();
        ODatabaseDocumentInternal db;
        String username;
        String password;
        if (session.isSignedIn()) {
            username = session.getUsername();
            password = session.getPassword();
        } else {
            username = settings.getDBUserName();
            password = settings.getDBUserPassword();
        }
        db = settings.getDatabasePoolFactory().get(settings.getDBUrl(), username, password).acquire();
        return db;
    }

    /**
     * Utility method to obtain {@link ODatabaseRecord} from {@link ODatabase}
     *
     * @param db
     * @return
     */
    public static ODatabaseDocument castToODatabaseDocument(ODatabase db) {
        while (db != null && !(db instanceof ODatabaseDocument)) {
            if (db instanceof ODatabaseInternal<?>) {
                db = ((ODatabaseInternal<?>) db).getUnderlying();
            }
        }
        return (ODatabaseDocument) db;
    }
}
