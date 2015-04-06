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

import org.apache.wicket.util.string.Strings;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.google.common.base.Converter;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

/**
 * Converter {@link OIndex}&lt;-&gt;{@link String} (full property name)
 */
public class OPropertyFullNameConverter extends Converter<OProperty, String> implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final OPropertyFullNameConverter INSTANCE = new OPropertyFullNameConverter();

    @Override
    protected String doForward(OProperty a) {
        return a.getFullName();
    }

    @Override
    protected OProperty doBackward(String b) {
        ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
        OSchema schema = db.getMetadata().getSchema();
        String className = Strings.beforeFirst(b, '.');
        String propertyName = Strings.afterFirst(b, '.');
        OClass oClass = schema.getClass(className);
        return oClass.getProperty(propertyName);
    }

}
