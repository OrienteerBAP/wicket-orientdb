package ru.ydn.wicket.wicketorientdb.security;

import com.orientechnologies.orient.core.metadata.security.ORule;

/**
 * Interface for checking access rights for a resource 
 */
public interface IResourceCheckingStrategy {
	
	public default boolean checkResource(ORule.ResourceGeneric resource, String specific, OrientPermission... permissions) {
		return checkResource(resource, specific, OrientPermission.combinedPermission(permissions));
	}
	public boolean checkResource(ORule.ResourceGeneric resource, String specific, int operation);
}
