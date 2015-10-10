package ru.ydn.wicket.wicketorientdb.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Iterators;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Useful utils over schema related objects
 */
public class OSchemaUtils {
	
	private OSchemaUtils() {
		
	}
	
	/**
	 * Check first several items to resolve common {@link OClass}
	 * @param it {@link Iterable} over {@link ODocument}s
	 * @param probeLimit limit over iterable
	 * @return common {@link OClass} or null
	 */
	public static OClass probeOClass(Iterable<ODocument> it, int probeLimit) {
		return probeOClass(it.iterator(), probeLimit);
	}
	
	/**
	 * Check first several items to resolve common {@link OClass}
	 * @param it {@link Iterable} over {@link ODocument}s
	 * @param probeLimit limit over iterable
	 * @return common {@link OClass} or null
	 */
	public static OClass probeOClass(Iterator<ODocument> it, int probeLimit) {
		return getCommonOClass(Iterators.limit(it, probeLimit));
	}
	
	/**
	 * Returns common {@link OClass} for a set of documents
	 * @param it {@link Iterable} over {@link ODocument}s
	 * @return common {@link OClass} or null
	 */
	public static OClass getCommonOClass(Iterable<ODocument> it) {
		return getCommonOClass(it.iterator());
	}
	
	/**
	 * Returns common {@link OClass} for a set of documents
	 * @param it {@link Iterator} over {@link ODocument}s
	 * @return common {@link OClass} or null
	 */
	public static OClass getCommonOClass(Iterator<ODocument> it) {
    	Set<OClass> candidates =  null;
    	OClass ret = null;
    	while(it.hasNext() && (candidates==null || !candidates.isEmpty())) {
    		ODocument doc = it.next();
    		OClass thisOClass = doc.getSchemaClass();
    		if(candidates==null){
    			candidates = new HashSet<OClass>();
    			candidates.add(thisOClass);
    			candidates.addAll(thisOClass.getAllSuperClasses());
    			ret = thisOClass;
    		}
    		else {
    			if(ret!=null && (ret.equals(thisOClass) || ret.isSuperClassOf(thisOClass))) continue;
    			else if(ret!=null && thisOClass.isSuperClassOf(ret)) {
    				ret = thisOClass;
    				candidates.clear();
    				candidates.add(ret);
    				candidates.addAll(ret.getAllSuperClasses());
    			}
    			else {
    				ret = null;
    				candidates.retainAll(thisOClass.getAllSuperClasses());
    			}
    		}
    	}
    	
    	if(ret==null && !candidates.isEmpty()) {
    		ret = getDeepestOClass(candidates);
    	}
    	
    	return ret;
    }
	
	/**
	 * Find the most deep {@link OClass} in a collection of classes
	 * @param classes {@link Iterable} with {@link OClass}es to handle
	 * @return deepest {@link OClass} or null
	 */
	public static OClass getDeepestOClass(Iterable<OClass> classes) {
		if(classes==null) return null;
		OClass ret=null;
		for(OClass oClass: classes) {
			if(ret==null) ret = oClass;
			else {
				if(!ret.equals(oClass) && oClass.isSubClassOf(ret)) ret = oClass;
			}
		}
		return ret;
	}
}
