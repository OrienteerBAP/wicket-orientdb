package ru.ydn.wicket.wicketorientdb.security;

import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.request.component.IRequestableComponent;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.util.string.Strings;

import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;
import com.orientechnologies.orient.core.metadata.security.OUser;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

/**
 * OrientDB specific {@link IAuthorizationStrategy}. It supports 3 types for components securing
 * <ul>
 * <li>Statically by {@link RequiredOrientResource} and {@link RequiredOrientResources} annotations</li>
 * <li>Dynamically by {@link ISecuredComponent}
 * <li>Dynamically by {@link Map}&lt;{@link String}, {@link OrientPermission}[]> object assigned to meta data key {@link OrientPermission}.REQUIRED_ORIENT_RESOURCES_KEY </li>
 * </ul> 
 */
public class OrientResourceAuthorizationStrategy  implements IAuthorizationStrategy
{

	@Override
	public <T extends IRequestableComponent> boolean isInstantiationAuthorized(
			Class<T> componentClass) {
		if(Page.class.isAssignableFrom(componentClass))
		{
			RequiredOrientResource[] resources = getRequiredOrientResources(componentClass);
			return resources!=null?checkResources(resources, Component.RENDER):true;
		}
		else
		{
			return true;
		}
	}

	@Override
	public boolean isActionAuthorized(Component component, Action action) {
		RequiredOrientResource[] resources = getRequiredOrientResources(component.getClass());
		if(resources!=null)
		{
			if(!checkResources(resources, action)) return false;
		}
		Map<String, OrientPermission[]> dynamicResources = component.getMetaData(OrientPermission.REQUIRED_ORIENT_RESOURCES_KEY);
		if(dynamicResources!=null)
		{
			if(!checkResources(dynamicResources, action)) return false;
		}
		if(component instanceof ISecuredComponent)
		{
			resources = ((ISecuredComponent)component).getRequiredResources();
			if(resources!=null)
			{
				if(!checkResources(resources, action)) return false;
			}
		}
		return true;
	}
	
	/**
	 * Check that current user has access to all mentioned resources
	 * @param resources {@link RequiredOrientResource}s to check
	 * @param action {@link Action} to check for
	 * @return true if access is allowed
	 */
	public boolean checkResources(RequiredOrientResource[] resources, Action action)
	{
		for (int i = 0; i < resources.length; i++) {
			RequiredOrientResource requiredOrientResource = resources[i];
			if(!checkResource(requiredOrientResource, action)) return false;
		}
		return true;
	}
	
	/**
	 * Check that current user has access to mentioned resource
	 * @param resource {@link RequiredOrientResource} to check
	 * @param action {@link Action} to check for
	 * @return true if access is allowed
	 */
	public boolean checkResource(RequiredOrientResource resource, Action action)
	{
		if(!resource.action().equals(action.getName())) return true;
		OUser user = OrientDbWebSession.get().getUser();
		if(user==null) return false;
		int iOperation = OrientPermission.combinedPermission(resource.permissions());
		ORule.ResourceGeneric value = OSecurityHelper.getResourceGeneric(resource.value());
		String specific = resource.specific();
		if(Strings.isEmpty(specific)) specific = null;
		if(user.checkIfAllowed(value, specific, iOperation)!=null) return true;
		while(!Strings.isEmpty(specific=Strings.beforeLastPathComponent(specific, '.')))
		{
			if(user.checkIfAllowed(value, specific+"."+ODatabaseSecurityResources.ALL, iOperation)!=null) return true;
		}
		return false;
	}
	
	/**
	 * Check that current user has access to all mentioned resources
	 * @param resources map with {@link OrientPermission}s to check
	 * @param action {@link Action} to check for
	 * @return true if access is allowed
	 */
	public boolean checkResources(Map<String, OrientPermission[]> resources, Action action)
	{
		for (Map.Entry<String, OrientPermission[]> entry : resources.entrySet()) {
			if(!checkResource(entry.getKey(), action, entry.getValue())) return false;
		}
		return true;
	}
	
	/**
	 * Check that current user has access to mentioned resource
	 * @param resource resource check
	 * @param action {@link Action} to check for
	 * @param permissions {@link OrientPermission}s to check
	 * @return true if access is allowed
	 */
	public boolean checkResource(String resource, Action action, OrientPermission[] permissions)
	{
		String actionName = action.getName();
		if(resource.indexOf(':')>0) {
			if(!(resource.endsWith(actionName) && resource.length()>actionName.length() 
					&& resource.charAt(resource.length()-actionName.length()-1) == ':')) return true;
		} else if(!Component.RENDER.equals(action)) return true; //Default suffix is for render: so other should be skipped
		
		OUser user = OrientDbWebSession.get().getUser();
		if(user==null) return false;
		ORule.ResourceGeneric generic = OSecurityHelper.getResourceGeneric(resource);
		if(generic==null) generic = ORule.mapLegacyResourceToGenericResource(resource);
		String specific = ORule.mapLegacyResourceToSpecificResource(resource);
		
		return user!=null
				?user.checkIfAllowed(generic, specific, OrientPermission.combinedPermission(permissions))!=null
				:false;
	}
	/**
	 * Extract {@link RequiredOrientResource}s from a Class
	 * @param clazz Class to extract {@link RequiredOrientResource}s from
	 * @return statically defined {@link RequiredOrientResource}s on specified class
	 */
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

	@Override
	public boolean isResourceAuthorized(IResource resource,
			PageParameters parameters) {
		RequiredOrientResource[] resources = getRequiredOrientResources(resource.getClass());
		if(resources!=null)
		{
			if(!checkResources(resources, Component.RENDER)) return false;
		}
		if(resource instanceof ISecuredComponent)
		{
			resources = ((ISecuredComponent)resource).getRequiredResources();
			if(resources!=null)
			{
				if(!checkResources(resources, Component.RENDER)) return false;
			}
		}
		return true;
	}

}
