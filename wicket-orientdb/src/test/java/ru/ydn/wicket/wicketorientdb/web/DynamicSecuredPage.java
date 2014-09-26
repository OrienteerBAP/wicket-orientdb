package ru.ydn.wicket.wicketorientdb.web;

import ru.ydn.wicket.wicketorientdb.security.ISecuredComponent;
import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

public class DynamicSecuredPage extends OrientDbTestPage implements ISecuredComponent
{
	@Override
	public RequiredOrientResource[] getRequiredResources() {
		return OSecurityHelper.requireOClass("ORole", OrientPermission.UPDATE);
	}

}
