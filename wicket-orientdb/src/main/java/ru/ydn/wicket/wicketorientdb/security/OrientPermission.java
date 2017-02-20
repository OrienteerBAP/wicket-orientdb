package ru.ydn.wicket.wicketorientdb.security;

import java.util.HashMap;
import org.apache.wicket.MetaDataKey;

/**
 * Wrapper enum over OrientDB permissions flat(int)
 */
public enum OrientPermission {
	CREATE(1), READ(2), UPDATE(4), DELETE(8), EXECUTE(16);
	
	private final int permissionFlag;
	
	public static final MetaDataKey<HashMap<String, OrientPermission[]>> REQUIRED_ORIENT_RESOURCES_KEY = new MetaDataKey<HashMap<String,OrientPermission[]>>() {

	private static final long serialVersionUID = 1L;};
	
	private OrientPermission(int permissionFlag)
	{
		this.permissionFlag=permissionFlag;
	}
	
	/**
	 * Calculates combined permissions flag
	 * @param permissions permissions to combine
	 * @return combined int flag for a permisssions
	 */
	public static int combinedPermission(OrientPermission... permissions)
	{
		int ret = 0;
		for (int i = 0; i < permissions.length; i++) {
			OrientPermission orientPermission = permissions[i];
			ret|=orientPermission.permissionFlag;
		}
		return ret;
	}
	
	public int getPermissionFlag()
	{
		return permissionFlag;
	}
}
