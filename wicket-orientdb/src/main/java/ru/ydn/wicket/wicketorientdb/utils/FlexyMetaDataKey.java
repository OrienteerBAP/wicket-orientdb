package ru.ydn.wicket.wicketorientdb.utils;

import java.io.Serializable;
import java.util.Objects;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;

/**
 * {@link MetaDataKey} implementation which allows to have objects as keys
 * @param <K> type of key
 * @param <V> type of value
 */
public final class FlexyMetaDataKey<K, V> extends MetaDataKey<V> {

	private static final long serialVersionUID = 1L;
	
	private final K key;
	
	public FlexyMetaDataKey(K key) {
		this.key = key;
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && Objects.equals(key, ((FlexyMetaDataKey<?, ?>)obj).key);
	}

	@Override
	public String toString() {
		return FlexyMetaDataKey.class.getName()+"("+key+")";
	}
	
	public static <K, V> MetaDataKey<V> asMetaDataKey(K key) {
		if(key instanceof MetaDataKey) return (MetaDataKey<V>) key;
		else return new FlexyMetaDataKey<K, V>(key);
	}
	
	public static <K, V> V get(Application app, K key) {
		return app.getMetaData(asMetaDataKey(key));
	}
	
	public static <K, V extends Serializable> V get(Session session, K key) {
		return session.getMetaData(asMetaDataKey(key));
	}
	
	public static <K, V extends Serializable> V get(Component cmp, K key) {
		return cmp.getMetaData(asMetaDataKey(key));
	}
	
	public static <K, V> Application set(Application app, K key, V value) {
		return app.setMetaData(asMetaDataKey(key), value);
	}
	
	public static <K, V extends Serializable> Session set(Session session, K key, V value) {
		return session.setMetaData(asMetaDataKey(key), value);
	}
	
	public static <K, V extends Serializable> Component set(Component cmp, K key, V value) {
		return cmp.setMetaData(asMetaDataKey(key), value);
	}

}
