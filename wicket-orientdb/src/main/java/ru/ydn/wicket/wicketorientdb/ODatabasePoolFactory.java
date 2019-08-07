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
 * Implementation of database pool factory.
 * Works like LRU cache, using {@link ConcurrentLinkedHashMap<PoolIdentity, ODatabasePool>} as store for databases pools
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
     * Get or create database pool instance for given user
     * @param database name of database
     * @param username name of user which need access to database
     * @param password user password
     * @return {@link ODatabasePool} which is new instance of pool or instance from pool storage
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
     * Close all open pools and clear pool storage
     */
    public void reset() {
        poolStore.forEach((identity, pool) -> pool.close());
        poolStore.clear();
    }

    /**
     * Close all open pools and clear pool storage. Set flag closed to true, so this instance can't be used again.
     * Need create new instance of {@link ODatabasePoolFactory} after close one of factories.
     */
    public void close() {
        if (!isClosed()) {
            closed = true;
            reset();
        }
    }

    /**
     * @return true if factory is closed
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * @return max pool size. Default is 64
     */
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * Set max pool size which will be used for create new {@link ODatabasePool}
     * @param maxPoolSize max pool size
     * @return this instance
     */
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
