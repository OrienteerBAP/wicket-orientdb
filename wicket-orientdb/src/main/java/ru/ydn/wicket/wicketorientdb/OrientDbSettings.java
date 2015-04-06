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

import java.util.ArrayList;
import java.util.List;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabasePoolBase;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;
import com.orientechnologies.orient.core.db.OPartitionedDatabasePoolFactory;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.network.OServerNetworkListener;
import com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpAbstract;

/**
 * Default implementation of {@link IOrientDbSettings}
 */
public class OrientDbSettings implements IOrientDbSettings {

    private String dbUrl;
    private String dbUserName;
    private String dbUserPassword;
    private String dbInstallatorUserName;
    private String dbInstallatorUserPassword;
    private String orientDbRestApiUrl;
    private OPartitionedDatabasePoolFactory poolFactory = new OPartitionedDatabasePoolFactory();

    private List<ORecordHook> oRecordHooks = new ArrayList<ORecordHook>();

    @Override
    public String getDBUrl() {
        return dbUrl;
    }

    @Override
    public OPartitionedDatabasePoolFactory getDatabasePoolFactory() {
        return poolFactory;
    }

    @Override
    public ODatabaseThreadLocalFactory getDatabaseThreadLocalFactory() {
        return Orient.instance().getDatabaseThreadFactory();
    }

    @Override
    public void setDBUrl(String url) {
        this.dbUrl = url;
    }

    @Override
    public void setDatabasePoolFactory(OPartitionedDatabasePoolFactory poolFactory) {
        this.poolFactory = poolFactory;
    }

    @Override
    public String getDBUserName() {
        return dbUserName;
    }

    @Override
    public String getDBUserPassword() {
        return dbUserPassword;
    }

    @Override
    public void setDBUserName(String userName) {
        this.dbUserName = userName;
    }

    @Override
    public void setDBUserPassword(String password) {
        this.dbUserPassword = password;
    }

    @Override
    public String getDBInstallatorUserName() {
        return dbInstallatorUserName;
    }

    @Override
    public String getDBInstallatorUserPassword() {
        return dbInstallatorUserPassword;
    }

    @Override
    public void setDBInstallatorUserName(String userName) {
        this.dbInstallatorUserName = userName;
    }

    @Override
    public void setDBInstallatorUserPassword(String password) {
        this.dbInstallatorUserPassword = password;
    }

    @Override
    public void setDatabaseThreadLocalFactory(
            ODatabaseThreadLocalFactory factory) {
        Orient.instance().registerThreadDatabaseFactory(factory);
    }

    @Override
    public List<ORecordHook> getORecordHooks() {
        return oRecordHooks;
    }

    @Override
    public String getOrientDBRestApiUrl() {
        if (orientDbRestApiUrl == null) {
            setOrientDBRestApiUrl(resolveOrientDBRestApiUrl());
        }
        return orientDbRestApiUrl;
    }

    @Override
    public void setOrientDBRestApiUrl(String orientDbRestApiUrl) {
        if (orientDbRestApiUrl != null && !orientDbRestApiUrl.endsWith("/")) {
            orientDbRestApiUrl += "/";
        }
        this.orientDbRestApiUrl = orientDbRestApiUrl;
    }

    public String resolveOrientDBRestApiUrl() {
        OrientDbWebApplication app = OrientDbWebApplication.get();
        OServer server = app.getServer();
        if (server != null) {
            OServerNetworkListener http = server.getListenerByProtocol(ONetworkProtocolHttpAbstract.class);
            if (http != null) {
                return "http://" + http.getListeningAddress(true);
            }
        }
        return null;
    }

}
