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

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.google.common.base.Converter;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

/**
 * Converter {@link OClass}&lt;-&gt;{@link String}
 */
public class OClassClassNameConverter extends Converter<OClass, String> implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final OClassClassNameConverter INSTANCE = new OClassClassNameConverter();

    @Override
    protected String doForward(OClass a) {
        return a.getName();
    }

    @Override
    protected OClass doBackward(String b) {
        ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
        OSchema schema = db.getMetadata().getSchema();
        return schema.getClass(b);
    }

}
