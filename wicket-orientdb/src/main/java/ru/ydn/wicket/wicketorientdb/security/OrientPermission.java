package ru.ydn.wicket.wicketorientdb.security;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.MetaDataKey;

public enum OrientPermission {
	CREATE(1), READ(2), UPDATE(4), DELETE(8);
	
	private final int permissionFlag;
	
	public static final MetaDataKey<HashMap<String, OrientPermission[]>> REQUIRED_ORIENT_RESOURCES_KEY = new MetaDataKey<HashMap<String,OrientPermission[]>>() {};
	
	private OrientPermission(int permissionFlag)
	{
		this.permissionFlag=permissionFlag;
	}
	
	public static int combinedPermission(OrientPermission... permissions)
	{
		int ret = 0;
		for (int i = 0; i < permissions.length; i++) {
			OrientPermission orientPermission = permissions[i];
			ret|=orientPermission.permissionFlag;
		}
		return ret;
	}
}
