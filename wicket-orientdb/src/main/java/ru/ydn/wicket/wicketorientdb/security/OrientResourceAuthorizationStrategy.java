/**
 * Copyright (C) 2015 Ilia Naryzhny (phantom@ydn.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.ydn.wicket.wicketorientdb.security;

import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.request.component.IRequestableComponent;
import org.apache.wicket.util.string.Strings;

import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORule;
import com.orientechnologies.orient.core.metadata.security.OUser;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

/**
 * OrientDB specific {@link IAuthorizationStrategy}. It supports 3 types for
 * components securing
 * <ul>
 * <li>Statically by {@link RequiredOrientResource} and
 * {@link RequiredOrientResources} annotations</li>
 * <li>Dynamically by {@link ISecuredComponent}
 * <li>Dynamically by {@link Map}&lt;{@link String}, {@link OrientPermission}[]>
 * object assigned to meta data key
 * {@link OrientPermission}.REQUIRED_ORIENT_RESOURCES_KEY </li>
 * </ul>
 */
public class OrientResourceAuthorizationStrategy implements IAuthorizationStrategy {

    @Override
    public <T extends IRequestableComponent> boolean isInstantiationAuthorized(
            Class<T> componentClass) {
        if (Page.class.isAssignableFrom(componentClass)) {
            RequiredOrientResource[] resources = getRequiredOrientResources(componentClass);
            return resources != null ? checkResources(resources) : true;
        } else {
            return true;
        }
    }

    @Override
    public boolean isActionAuthorized(Component component, Action action) {
        if (action.equals(Component.RENDER)) {
            RequiredOrientResource[] resources = getRequiredOrientResources(component.getClass());
            if (resources != null) {
                if (!checkResources(resources)) {
                    return false;
                }
            }
            Map<String, OrientPermission[]> dynamicResources = component.getMetaData(OrientPermission.REQUIRED_ORIENT_RESOURCES_KEY);
            if (dynamicResources != null) {
                if (!checkResources(dynamicResources)) {
                    return false;
                }
            }
            if (component instanceof ISecuredComponent) {
                resources = ((ISecuredComponent) component).getRequiredResources();
                if (resources != null) {
                    if (!checkResources(resources)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return true;
        }
    }

    /**
     * Check that current user has access to all mentioned resources
     *
     * @param resources
     * @return
     */
    public boolean checkResources(RequiredOrientResource[] resources) {
        for (int i = 0; i < resources.length; i++) {
            RequiredOrientResource requiredOrientResource = resources[i];
            if (!checkResource(requiredOrientResource)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check that current user has access to mentioned resource
     *
     * @param resource
     * @return
     */
    public boolean checkResource(RequiredOrientResource resource) {
        OUser user = OrientDbWebSession.get().getUser();
        if (user == null) {
            return false;
        }
        int iOperation = OrientPermission.combinedPermission(resource.permissions());
        ORule.ResourceGeneric value = OSecurityHelper.getResourceGeneric(resource.value());
        String specific = resource.specific();
        if (Strings.isEmpty(specific)) {
            specific = null;
        }
        if (user.checkIfAllowed(value, specific, iOperation) != null) {
            return true;
        }
        while (!Strings.isEmpty(specific = Strings.beforeLastPathComponent(specific, '.'))) {
            if (user.checkIfAllowed(value, specific + "." + ODatabaseSecurityResources.ALL, iOperation) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check that current user has access to all mentioned resources
     *
     * @param resources
     * @return
     */
    public boolean checkResources(Map<String, OrientPermission[]> resources) {
        for (Map.Entry<String, OrientPermission[]> entry : resources.entrySet()) {
            if (!checkResource(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check that current user has access to mentioned resource
     *
     * @param resource
     * @return
     */
    public boolean checkResource(String resource, OrientPermission[] permissions) {
        OUser user = OrientDbWebSession.get().getUser();
        if (user == null) {
            return false;
        }
        ORule.ResourceGeneric generic = OSecurityHelper.getResourceGeneric(resource);
        if (generic == null) {
            generic = ORule.mapLegacyResourceToGenericResource(resource);
        }
        String specific = ORule.mapLegacyResourceToSpecificResource(resource);

        return user != null
                ? user.checkIfAllowed(generic, specific, OrientPermission.combinedPermission(permissions)) != null
                : false;
    }

    /**
     * @param clazz
     * @return statically defined {@link RequiredOrientResource}s on specified
     * class
     */
    public RequiredOrientResource[] getRequiredOrientResources(Class<?> clazz) {
        RequiredOrientResources resources = clazz.getAnnotation(RequiredOrientResources.class);
        RequiredOrientResource singleResource = clazz.getAnnotation(RequiredOrientResource.class);
        if (resources == null && singleResource == null) {
            return null;
        }
        if (resources != null && singleResource == null) {
            return resources.value();
        }
        if (resources == null && singleResource != null) {
            return new RequiredOrientResource[]{singleResource};
        }
        if (resources != null && singleResource != null) {
            RequiredOrientResource[] ret = new RequiredOrientResource[resources.value().length + 1];
            ret[0] = singleResource;
            System.arraycopy(resources.value(), 0, ret, 1, resources.value().length);
            return ret;
        }
        return null;
    }

}
