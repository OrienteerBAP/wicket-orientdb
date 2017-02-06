package ru.ydn.wicket.wicketorientdb.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * {@link ODocumentWrapper} for representing of {@link ODocument} as {@link Map}
 */
public class ODocumentMapWrapper extends ODocumentWrapper implements Map<String, Object>
{
	private static final long serialVersionUID = 1L;

	public ODocumentMapWrapper() {
		super();
	}

	public ODocumentMapWrapper(ODocument iDocument) {
		super(iDocument);
	}

	public ODocumentMapWrapper(ORID iRID) {
		super(iRID);
	}

	public ODocumentMapWrapper(String iClassName) {
		super(iClassName);
	}

	@Override
	public int size() {
		return document.fieldNames().length;
	}

	@Override
	public boolean isEmpty() {
		return size()==0;
	}

	@Override
	public boolean containsKey(Object key) {
		return key instanceof String?document.containsField((String)key):false;
	}

	@Override
	public boolean containsValue(Object value) {
		return document.toMap().containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return key instanceof String?document.field((String)key):null;
	}

	@Override
	public Object put(String key, Object value) {
		Object ret = get(key);
		document.field(key, value);
		return ret;
	}

	@Override
	public Object remove(Object key) {
		Object ret = get(key);
		if(key instanceof String) document.removeField((String)key);
		return ret;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		for (Map.Entry<? extends String, ? extends Object> newData : m.entrySet()) {
			put(newData.getKey(), newData.getValue());
		}
	}

	@Override
	public void clear() {
		for (String field : document.fieldNames()) {
			remove(field);
		}
	}

	@Override
	public Set<String> keySet() {
		return new HashSet<String>(Arrays.asList(document.fieldNames()));
	}

	@Override
	public Collection<Object> values() {
		List<Object> ret = new ArrayList<Object>();
		for(java.util.Map.Entry<String, Object> entry : document)
		{
			ret.add(entry.getValue());
		}
		return ret;
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		Set<java.util.Map.Entry<String, Object>> ret = new HashSet<java.util.Map.Entry<String, Object>>();
		for(java.util.Map.Entry<String, Object> entry : document)
		{
			ret.add(entry);
		}
		return ret;
	}

}
