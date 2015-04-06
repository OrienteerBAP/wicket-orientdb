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
package ru.ydn.wicket.wicketorientdb.utils;

import java.io.Serializable;

import com.google.common.base.Function;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

/**
 * {@link Function} for obtaing of custom property from {@link OProperty}
 */
public class GetCustomFromOPropertyFunction implements Function<OProperty, String>, Serializable {

    private static final long serialVersionUID = 1L;
    private final String customName;

    public GetCustomFromOPropertyFunction(String customName) {
        this.customName = customName;
    }

    @Override
    public String apply(OProperty input) {
        return input.getCustom(customName);
    }

}
