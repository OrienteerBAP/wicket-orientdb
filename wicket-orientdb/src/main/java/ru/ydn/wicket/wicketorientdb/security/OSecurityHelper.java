package ru.ydn.wicket.wicketorientdb.security;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.exception.OSecurityAccessException;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORestrictedAccessHook;
import com.orientechnologies.orient.core.metadata.security.OSecurityShared;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Helper class for security functionality
 */
public class OSecurityHelper 
{
	private static final Map<OrientPermission, String> MAPPING_FOR_HACK = new HashMap<OrientPermission, String>();
	static
	{
		MAPPING_FOR_HACK.put(OrientPermission.READ, OSecurityShared.ALLOW_READ_FIELD);
		MAPPING_FOR_HACK.put(OrientPermission.UPDATE, OSecurityShared.ALLOW_UPDATE_FIELD);
		MAPPING_FOR_HACK.put(OrientPermission.DELETE, OSecurityShared.ALLOW_DELETE_FIELD);
	}
	
	private static class RequiredOrientResourceImpl implements RequiredOrientResource
	{
		private final String value;
		private final OrientPermission[] permissions;
		
		public RequiredOrientResourceImpl(String value, OrientPermission[] permissions)
		{
			this.value = value;
			this.permissions = permissions;
		}
		
		@Override
		public Class<? extends Annotation> annotationType() {
			return RequiredOrientResource.class;
		}

		@Override
		public String value() {
			return value;
		}

		@Override
		public OrientPermission[] permissions() {
			return permissions;
		}
		
	}
	/**
	 * @param oClass subject {@link OClass} for security check
	 * @param permissions required permissions for access {@link OClass}
	 * @return
	 */
	public static RequiredOrientResource[] requireOClass(final OClass oClass, final OrientPermission... permissions)
	{
		return requireOClass(oClass.getName(), permissions);
	}
	/**
	 * @param oClassName name of the subject {@link OClass} for security check
	 * @param permissions required permissions for access {@link OClass}
	 * @return
	 */
	public static RequiredOrientResource[] requireOClass(final String oClassName, final OrientPermission... permissions)
	{
		return requireResource(ODatabaseSecurityResources.CLASS+"."+oClassName, permissions);
	}
	/**
	 * @param resource
	 * @param permissions
	 * @return
	 */
	public static RequiredOrientResource[] requireResource(final String resource, final OrientPermission... permissions)
	{
		return new RequiredOrientResource[]{new RequiredOrientResourceImpl(resource, permissions)};
	}
	
	//Very bad hack - should be changed in OrientDB
	private static class AccessToIsAllowedInRestrictedAccessHook extends ORestrictedAccessHook
	{
		public static AccessToIsAllowedInRestrictedAccessHook INSTANCE = new AccessToIsAllowedInRestrictedAccessHook();
		@Override
		public boolean isAllowed(ODocument iDocument,
				String iAllowOperation, boolean iReadOriginal) {
			return super.isAllowed(iDocument, iAllowOperation, iReadOriginal);
		}
		
	}
	
	/**
	 * Check that all required permissions present for specified {@link ODocument}
	 * @param doc
	 * @param permissions
	 * @return
	 */
	public static boolean isAllowed(ODocument doc, OrientPermission... permissions)
	{
		if(!isAllowed(doc.getSchemaClass(), permissions)) return false;
		for (OrientPermission orientPermission : permissions) {
			String allowOperation = MAPPING_FOR_HACK.get(orientPermission);
			if(allowOperation!=null)
			{
				if(!AccessToIsAllowedInRestrictedAccessHook.INSTANCE.isAllowed(doc, allowOperation, false)) return false;
			}
		}
		return true;
	}
	/**
	 * Check that all required permissions present for specified {@link OClass}
	 * @param doc
	 * @param permissions
	 * @return
	 */
	public static boolean isAllowed(OClass oClass, OrientPermission... permissions)
	{
		int iOperation = OrientPermission.combinedPermission(permissions);
		ODatabaseRecord db = OrientDbWebSession.get().getDatabase();
		try {
			db.checkSecurity(ODatabaseSecurityResources.CLASS, iOperation, oClass.getName());
			return true;
		} catch (OSecurityAccessException e) {
			return false;
		}
	}
	
}
