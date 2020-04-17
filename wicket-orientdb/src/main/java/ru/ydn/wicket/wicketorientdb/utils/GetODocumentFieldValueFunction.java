package ru.ydn.wicket.wicketorientdb.utils;

import java.io.Serializable;

import com.google.common.base.Function;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link Function} to get field value from ODocument
 *
 * @param <V> field value type
 */
public class GetODocumentFieldValueFunction<V> implements Function<ODocument, V>, Serializable {

	private static final long serialVersionUID = 1L;
	private final String fieldName;
	
	public GetODocumentFieldValueFunction(String fieldName) {
		this.fieldName = fieldName;
	}
	
	@Override
	public V apply(ODocument input) {
		return input.field(fieldName);
	}

}
