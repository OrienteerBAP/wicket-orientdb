package ru.ydn.wicket.wicketorientdb.utils;

import java.util.Comparator;
import java.util.Objects;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Simple {@link ODocument}s comparator by value of fields
 */
public class ODocumentsByFieldComparator implements Comparator<ODocument>{

	private final String fieldName;
	
	public ODocumentsByFieldComparator(String fieldName) {
		this.fieldName = fieldName;
	}
	@SuppressWarnings("unchecked")
	@Override
	public int compare(ODocument doc1, ODocument doc2) {
		Object val1 = doc1.field(fieldName);
		Object val2 = doc2.field(fieldName);
		if(val1==val2 || Objects.equals(val1, val2)) return 0;
		else if(val1!=null && val1 instanceof Comparable) return ((Comparable<Object>)val1).compareTo(val2);
		else if(val2!=null && val2 instanceof Comparable)  return -((Comparable<Object>)val2).compareTo(val1);
		else return 1;
	}

}
