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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.util.string.Strings;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.exception.OSecurityAccessException;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ODatabaseSecurityResources;
import com.orientechnologies.orient.core.metadata.security.ORestrictedAccessHook;
import com.orientechnologies.orient.core.metadata.security.ORule;
import com.orientechnologies.orient.core.metadata.security.OSecurityShared;
import com.orientechnologies.orient.core.metadata.security.OUser;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Helper class for security functionality
 */
public class OSecurityHelper {

    public static final String FUNCTION = "FUNCTION";
    public static final String CLASS = "CLASS";
    public static final String CLUSTER = "CLUSTER";
    public static final String BYPASS_RESTRICTED = "BYPASS_RESTRICTED";
    public static final String DATABASE = "DATABASE";
    public static final String SCHEMA = "SCHEMA";
    public static final String COMMAND = "COMMAND";
    public static final String RECORD_HOOK = "RECORD_HOOK";

    private static final Map<OrientPermission, String> MAPPING_FOR_HACK = new HashMap<OrientPermission, String>();

    static {
        MAPPING_FOR_HACK.put(OrientPermission.READ, OSecurityShared.ALLOW_READ_FIELD);
        MAPPING_FOR_HACK.put(OrientPermission.UPDATE, OSecurityShared.ALLOW_UPDATE_FIELD);
        MAPPING_FOR_HACK.put(OrientPermission.DELETE, OSecurityShared.ALLOW_DELETE_FIELD);
    }

    private static class RequiredOrientResourceImpl implements RequiredOrientResource {

        private final String value;
        private final String specific;
        private final OrientPermission[] permissions;

        public RequiredOrientResourceImpl(String value, String specific, OrientPermission[] permissions) {
            this.value = value;
            this.specific = specific;
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
        public String specific() {
            return specific;
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
    public static RequiredOrientResource[] requireOClass(final OClass oClass, final OrientPermission... permissions) {
        return requireOClass(oClass.getName(), permissions);
    }

    /**
     * @param oClassName name of the subject {@link OClass} for security check
     * @param permissions required permissions for access {@link OClass}
     * @return
     */
    public static RequiredOrientResource[] requireOClass(final String oClassName, final OrientPermission... permissions) {
        return requireResource(ORule.ResourceGeneric.CLASS, oClassName, permissions);
    }

    /**
     * @param resource
     * @param permissions
     * @return
     */
    public static RequiredOrientResource[] requireResource(final ORule.ResourceGeneric resource, final String specific, final OrientPermission... permissions) {
        return new RequiredOrientResource[]{new RequiredOrientResourceImpl(resource.getName(), specific, permissions)};
    }

    //Very bad hack - should be changed in OrientDB
    private static class AccessToIsAllowedInRestrictedAccessHook extends ORestrictedAccessHook {

        public static final AccessToIsAllowedInRestrictedAccessHook INSTANCE = new AccessToIsAllowedInRestrictedAccessHook();

        @Override
        public boolean isAllowed(ODocument iDocument,
                String iAllowOperation, boolean iReadOriginal) {
            return super.isAllowed(iDocument, iAllowOperation, iReadOriginal);
        }

    }

    /**
     * Check that all required permissions present for specified
     * {@link ODocument}
     *
     * @param doc
     * @param permissions
     * @return
     */
    public static boolean isAllowed(ODocument doc, OrientPermission... permissions) {
        if (!isAllowed(doc.getSchemaClass(), permissions)) {
            return false;
        }
        for (OrientPermission orientPermission : permissions) {
            String allowOperation = MAPPING_FOR_HACK.get(orientPermission);
            if (allowOperation != null) {
                if (!AccessToIsAllowedInRestrictedAccessHook.INSTANCE.isAllowed(doc, allowOperation, false)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check that all required permissions present for specified {@link OClass}
     *
     * @param doc
     * @param permissions
     * @return
     */
    public static boolean isAllowed(OClass oClass, OrientPermission... permissions) {
        return isAllowed(ORule.ResourceGeneric.CLASS, oClass.getName(), permissions);
    }

    /**
     * Check that all required permissions present for specified resource and
     * specific
     *
     * @param resource
     * @param specific
     * @param permissions
     * @return
     */
    public static boolean isAllowed(ORule.ResourceGeneric resource, String specific, OrientPermission... permissions) {
        return OrientDbWebSession.get().getEffectiveUser()
                .checkIfAllowed(resource, specific, OrientPermission.combinedPermission(permissions)) != null;
    }

    public static <T extends Component> T secureComponent(T component, RequiredOrientResource... resources) {
        return secureComponent(component, toSecureMap(resources));
    }

    public static <T extends Component> T secureComponent(T component, HashMap<String, OrientPermission[]> secureMap) {
        component.setMetaData(OrientPermission.REQUIRED_ORIENT_RESOURCES_KEY, secureMap);
        return component;
    }

    public static HashMap<String, OrientPermission[]> toSecureMap(RequiredOrientResource... resources) {
        HashMap<String, OrientPermission[]> secureMap = new HashMap<String, OrientPermission[]>();
        for (RequiredOrientResource requiredOrientResource : resources) {
            String resource = requiredOrientResource.value();
            String specific = requiredOrientResource.specific();
            if (!Strings.isEmpty(specific)) {
                resource = resource + "." + specific;
            }
            secureMap.put(resource, requiredOrientResource.permissions());
        }
        return secureMap;
    }

    public static ORule.ResourceGeneric getResourceGeneric(String name) {
        ORule.ResourceGeneric value = ORule.ResourceGeneric.valueOf(name);
        if (value == null) {
            value = ORule.mapLegacyResourceToGenericResource(name);
        }
        return value;
    }

}
