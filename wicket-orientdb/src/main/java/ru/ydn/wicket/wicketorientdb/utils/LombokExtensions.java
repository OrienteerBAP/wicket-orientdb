package ru.ydn.wicket.wicketorientdb.utils;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.request.Request;

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
		return user!=null?getDocument(user.getIdentity().getIdentity()):null;
	}
	
	/**
	 * Extract ODocument from {@link OSecurityRole}
	 * @param role object to extract from
	 * @return {@link ODocument}
	 */
	public ODocument getDocument(OSecurityRole role) {
		return role!=null?getDocument(role.getIdentity().getIdentity()):null;
	}
	
	public HttpServletRequest asHttpServletRequest(Request request) {
		if(request==null) return null;
		else return (HttpServletRequest) request.getContainerRequest();
	}
	
	private ODocument getDocument(OIdentifiable identifiable) {
		if(identifiable==null || identifiable instanceof ODocument) return (ODocument) identifiable;
		ODocument ret = identifiable.getRecord();
		if(ret == null) ret = DBClosure.sudoLoad(identifiable.getIdentity());
		return ret;
	}
}
