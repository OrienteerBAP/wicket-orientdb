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

import java.util.List;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabasePoolBase;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;
import com.orientechnologies.orient.core.db.OPartitionedDatabasePoolFactory;
import com.orientechnologies.orient.core.hook.ORecordHook;

/**
 * OrientDB setting to be used in Wicket-OrientDB application
 */
public interface IOrientDbSettings {

    /**
     * @return URL to connect to the OrientDB
     */
    public String getDBUrl();

    /**
     * @return Default DB username
     */
    public String getDBUserName();

    /**
     * @return Password for default user
     */
    public String getDBUserPassword();

    /**
     * @return Username for user which should be used for administrative tasks
     */
    public String getDBInstallatorUserName();

    /**
     * @return Password for the user which should be used for administrative
     * tasks
     */
    public String getDBInstallatorUserPassword();

    /**
     * @return {@link OPartitionedDatabasePoolFactory} for DB pool for the
     * application
     */
    public OPartitionedDatabasePoolFactory getDatabasePoolFactory();

    /**
     * @return factory for {@link ODatabaseRecord}
     */
    public ODatabaseThreadLocalFactory getDatabaseThreadLocalFactory();

    /**
     * @return default url for orientdb rest API
     */
    public String getOrientDBRestApiUrl();

    /**
     * Set URL for the OrientDB
     *
     * @param url
     */
    public void setDBUrl(String url);

    /**
     * Set username for default user
     *
     * @param userName
     */
    public void setDBUserName(String userName);

    /**
     * Set password for default user
     *
     * @param password
     */
    public void setDBUserPassword(String password);

    /**
     * Set username for user which will be used for admin stuff
     *
     * @param userName
     */
    public void setDBInstallatorUserName(String userName);

    /**
     * Set password for user which will be used for admin stuff
     *
     * @param password
     */
    public void setDBInstallatorUserPassword(String password);

    /**
     * Set {@link ODatabasePoolBase} which should be used for DB connections
     * pooling
     *
     * @param pool
     */
    public void setDatabasePoolFactory(OPartitionedDatabasePoolFactory poolFactory);

    /**
     * Set {@link ODatabaseThreadLocalFactory} which should be used for
     * obtaining {@link ODatabaseRecord}
     *
     * @param factory
     */
    public void setDatabaseThreadLocalFactory(ODatabaseThreadLocalFactory factory);

    /**
     * Set OrientDB Rest API URL
     */
    public void setOrientDBRestApiUrl(String orientDbRestApiUrl);

    /**
     * @return {@link List} of {@link ORecordHook} which should be registered
     * for every DB instance created
     */
    public List<ORecordHook> getORecordHooks();
}
