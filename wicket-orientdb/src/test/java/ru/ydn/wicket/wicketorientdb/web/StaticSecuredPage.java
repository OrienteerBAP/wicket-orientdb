package ru.ydn.wicket.wicketorientdb.web;

import ru.ydn.wicket.wicketorientdb.security.OrientPermission;
import ru.ydn.wicket.wicketorientdb.security.RequiredOrientResource;

@RequiredOrientResource(value="CLASS", specific="ORole", permissions=OrientPermission.UPDATE)
public class StaticSecuredPage extends OrientDbTestPage
{
	private static final long serialVersionUID = 1L;

}
