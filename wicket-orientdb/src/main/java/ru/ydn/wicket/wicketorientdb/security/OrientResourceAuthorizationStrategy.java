package ru.ydn.wicket.wicketorientdb.security;

import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.request.component.IRequestableComponent;

import com.orientechnologies.orient.core.metadata.security.OUser;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

public class OrientResourceAuthorizationStrategy  implements IAuthorizationStrategy
{

	@Override
	public <T extends IRequestableComponent> boolean isInstantiationAuthorized(
			Class<T> componentClass) {
		if(Page.class.isAssignableFrom(componentClass))
		{
			RequiredOrientResource[] resources = getRequiredOrientResources(componentClass);
			return resources!=null?checkResources(resources):true;
		}
		else
		{
			return true;
		}
	}

	@Override
	public boolean isActionAuthorized(Component component, Action action) {
		if(action.equals(Component.RENDER))
		{
			RequiredOrientResource[] resources = getRequiredOrientResources(component.getClass());
			if(resources!=null)
			{
				if(!checkResources(resources)) return false;
			}
			Map<String, OrientPermission[]> dynamicResources = component.getMetaData(OrientPermission.REQUIRED_ORIENT_RESOURCES_KEY);
			if(dynamicResources!=null)
			{
				if(!checkResources(dynamicResources)) return false;
			}
			return true;
		}
		else
		{
			return true;
		}
	}
	
	public boolean checkResources(RequiredOrientResource[] resources)
	{
		for (int i = 0; i < resources.length; i++) {
			RequiredOrientResource requiredOrientResource = resources[i];
			if(!checkResource(requiredOrientResource)) return false;
		}
		return true;
	}
	
	public boolean checkResource(RequiredOrientResource resource)
	{
		OUser user = OrientDbWebSession.get().getUser();
		return user!=null
				?user.checkIfAllowed(resource.value(), OrientPermission.combinedPermission(resource.permissions()))!=null
				:false;
	}
	
	public boolean checkResources(Map<String, OrientPermission[]> resources)
	{
		for (Map.Entry<String, OrientPermission[]> entry : resources.entrySet()) {
			if(!checkResource(entry.getKey(), entry.getValue())) return false;
		}
		return true;
	}
	
	public boolean checkResource(String resource, OrientPermission[] permissions)
	{
		OUser user = OrientDbWebSession.get().getUser();
		return user!=null
				?user.checkIfAllowed(resource, OrientPermission.combinedPermission(permissions))!=null
				:false;
	}
	
	public RequiredOrientResource[] getRequiredOrientResources(Class<?> clazz)
	{
		RequiredOrientResources resources = clazz.getAnnotation(RequiredOrientResources.class);
		RequiredOrientResource singleResource = clazz.getAnnotation(RequiredOrientResource.class);
		if(resources==null && singleResource==null) return null;
		if(resources!=null && singleResource==null) return resources.value();
		if(resources==null && singleResource!=null) return new RequiredOrientResource[]{singleResource};
		if(resources!=null && singleResource!=null)
		{
			RequiredOrientResource[] ret = new RequiredOrientResource[resources.value().length+1];
			ret[0]=singleResource;
			System.arraycopy(resources.value(), 0, ret, 1, resources.value().length);
			return ret;
		}
		return null;
	}

}
