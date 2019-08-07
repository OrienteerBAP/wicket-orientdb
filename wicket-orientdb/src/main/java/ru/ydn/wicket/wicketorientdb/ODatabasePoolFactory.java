package ru.ydn.wicket.wicketorientdb;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.orientechnologies.orient.core.OOrientListenerAbstract;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabasePool;

import java.util.Objects;

/**
 *
 */
public class ODatabasePoolFactory extends OOrientListenerAbstract {

    private boolean closed;
    private final ConcurrentLinkedHashMap<PoolIdentity, ODatabasePool> poolStore;

    public ODatabasePoolFactory() {
        this(100);
    }

    public ODatabasePoolFactory(int capacity) {
        poolStore = new ConcurrentLinkedHashMap.Builder<PoolIdentity, ODatabasePool>()
                .maximumWeightedCapacity(capacity)
                .listener((identity, databasePool) -> databasePool.close())
                .build();

        Orient.instance().registerWeakOrientStartupListener(this);
        Orient.instance().registerWeakOrientShutdownListener(this);
    }

    /**
     *
     * @param url
     * @param username
     * @param password
     * @return
     */
    public ODatabasePool get(String url, String username, String password) {
        checkForClose();

        PoolIdentity identity = new PoolIdentity(url, username, password);
        ODatabasePool pool = poolStore.get(identity);
        if (pool != null) {
            return pool;
        }

        return poolStore.computeIfAbsent(identity, indent -> new ODatabasePool(
                    OrientDbWebApplication.lookupApplication().getServer().getContext(),
                    identity.url, identity.username, identity.password
                )
        );
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
    }
}
