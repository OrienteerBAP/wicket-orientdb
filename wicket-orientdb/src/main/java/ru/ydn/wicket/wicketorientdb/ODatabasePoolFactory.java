package ru.ydn.wicket.wicketorientdb;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.orientechnologies.orient.core.OOrientListenerAbstract;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.db.OrientDBConfigBuilder;

import java.util.Objects;

/**
 *
 */
public class ODatabasePoolFactory extends OOrientListenerAbstract {

    private volatile int maxPoolSize = 64;

    private boolean closed;
    private final ConcurrentLinkedHashMap<PoolIdentity, ODatabasePool> poolStore;
    private final OrientDB orientDB;

    public ODatabasePoolFactory(OrientDB orientDB) {
        this(orientDB, 100);
    }

    public ODatabasePoolFactory(OrientDB orientDB, int capacity) {
        poolStore = new ConcurrentLinkedHashMap.Builder<PoolIdentity, ODatabasePool>()
                .maximumWeightedCapacity(capacity)
                .listener((identity, databasePool) -> databasePool.close())
                .build();
        this.orientDB = orientDB;

        Orient.instance().registerWeakOrientStartupListener(this);
        Orient.instance().registerWeakOrientShutdownListener(this);
    }

    /**
     *
     * @param database
     * @param username
     * @param password
     * @return
     */
    public ODatabasePool get(String database, String username, String password) {
        checkForClose();

        PoolIdentity identity = new PoolIdentity(database, username, password);
        ODatabasePool pool = poolStore.get(identity);
        if (pool != null) {
            return pool;
        }

        return poolStore.computeIfAbsent(identity, indent -> {
            OrientDBConfigBuilder builder = OrientDBConfig.builder();
            builder.addConfig(OGlobalConfiguration.DB_POOL_MAX, maxPoolSize);
            return new ODatabasePool(orientDB, identity.url, identity.username, identity.password, builder.build());
        });
    }

    /**
     *
     */
    public void reset() {
        poolStore.forEach((identity, pool) -> pool.close());
        poolStore.clear();
    }

    public void close() {
        if (!isClosed()) {
            closed = true;
            reset();
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public ODatabasePoolFactory setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    @Override
    public void onShutdown() {
        close();
    }

    private void checkForClose() {
        if (closed)
            throw new IllegalStateException("Pool factory is closed");
    }

    private static class PoolIdentity {
        private final String url;
        private final String username;
        private final String password;

        public PoolIdentity(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PoolIdentity that = (PoolIdentity) o;
            return Objects.equals(url, that.url) &&
                    Objects.equals(username, that.username) &&
                    Objects.equals(password, that.password);
        }

        @Override
        public int hashCode() {
            return Objects.hash(url, username, password);
        }

        @Override
        public String toString() {
            return "PoolIdentity{" +
                    "url='" + url + '\'' +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }
}
