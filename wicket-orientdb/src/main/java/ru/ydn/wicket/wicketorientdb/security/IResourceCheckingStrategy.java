package ru.ydn.wicket.wicketorientdb.security;

import com.orientechnologies.orient.core.metadata.security.ORule;

/**
 * Interface for checking access rights for a resource 
 */
public interface IResourceCheckingStrategy {
	
	public boolean checkResource(ORule.ResourceGeneric resource, String specific, OrientPermission... permissions);
}
