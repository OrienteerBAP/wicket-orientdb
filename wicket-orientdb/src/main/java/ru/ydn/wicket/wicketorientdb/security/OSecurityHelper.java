package ru.ydn.wicket.wicketorientdb.security;

import java.util.HashMap;
import java.util.Map;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.exception.OSecurityAccessException;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORestrictedAccessHook;
import com.orientechnologies.orient.core.metadata.security.OSecurityShared;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class OSecurityHelper 
{
	private static final Map<OrientPermission, String> MAPPING_FOR_HACK = new HashMap<>();
	static
	{
		MAPPING_FOR_HACK.put(OrientPermission.READ, OSecurityShared.ALLOW_READ_FIELD);
		MAPPING_FOR_HACK.put(OrientPermission.UPDATE, OSecurityShared.ALLOW_UPDATE_FIELD);
		MAPPING_FOR_HACK.put(OrientPermission.DELETE, OSecurityShared.ALLOW_DELETE_FIELD);
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
