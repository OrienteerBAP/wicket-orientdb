package ru.ydn.wicket.wicketorientdb.utils;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.security.OSecurityRole;
import com.orientechnologies.orient.core.metadata.security.OSecurityUser;
import com.orientechnologies.orient.core.record.impl.ODocument;

import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;

/**
 * Lombok extention class to simplify work with source code.
 * Should be added as {@link ExtensionMethod} 
 */
@UtilityClass
public final class LombokExtensions {
	
	/**
	 * Extract ODocument from {@link OSecurityUser}
	 * @param user object to extract from
	 * @return {@link ODocument}
	 */
	public ODocument getDocument(OSecurityUser user) {
		if(user==null) return null;
		OIdentifiable userId = user.getIdentity();
		return userId!=null?userId.getRecord():null;
	}
	
	/**
	 * Extract ODocument from {@link OSecurityRole}
	 * @param role object to extract from
	 * @return {@link ODocument}
	 */
	public ODocument getDocument(OSecurityRole role) {
		if(role==null) return null;
		OIdentifiable roleId = role.getIdentity();
		return roleId!=null?roleId.getRecord():null;
	}
}
