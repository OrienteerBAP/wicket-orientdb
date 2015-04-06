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

import java.util.HashMap;
import org.apache.wicket.MetaDataKey;

/**
 * Wrapper enum over OrientDB permissions flat(int)
 */
public enum OrientPermission {

    CREATE(1), READ(2), UPDATE(4), DELETE(8);

    private final int permissionFlag;

    public static final MetaDataKey<HashMap<String, OrientPermission[]>> REQUIRED_ORIENT_RESOURCES_KEY = new MetaDataKey<HashMap<String, OrientPermission[]>>() {

        private static final long serialVersionUID = 1L;
    };

    private OrientPermission(int permissionFlag) {
        this.permissionFlag = permissionFlag;
    }

    /**
     * Calculates combined permissions flag
     *
     * @param permissions
     * @return
     */
    public static int combinedPermission(OrientPermission... permissions) {
        int ret = 0;
        for (int i = 0; i < permissions.length; i++) {
            OrientPermission orientPermission = permissions[i];
            ret |= orientPermission.permissionFlag;
        }
        return ret;
    }

    public int getPermissionFlag() {
        return permissionFlag;
    }
}
