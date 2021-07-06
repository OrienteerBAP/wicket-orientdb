package ru.ydn.wicket.wicketorientdb.model;

import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * Interface to mark objects and components which are aware about underling OClass
 */
public interface IOClassAware {

	/**
	 * Return OClass about which this object is aware of
	 * @return OClass or null
	 */
	public OClass getSchemaClass();
}
