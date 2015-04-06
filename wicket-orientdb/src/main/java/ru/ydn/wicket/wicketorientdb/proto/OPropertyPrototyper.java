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
package ru.ydn.wicket.wicketorientdb.proto;

import java.util.Arrays;
import java.util.List;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;

/**
 * Prototyper for {@link OProperty}
 */
public class OPropertyPrototyper extends AbstractPrototyper<OProperty> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String LINKED_TYPE = "linkedType";
    public static final String LINKED_CLASS = "linkedClass";
    public static final String MANDATORY = "mandatory";
    public static final String READONLY = "readonly";
    public static final String NOT_NULL = "notNull";
    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String REGEXP = "regexp";
    public static final String COLLATE = "collate";
    public static final List<String> OPROPERTY_ATTRS = Arrays.asList(NAME, TYPE, LINKED_TYPE, LINKED_CLASS, MANDATORY, READONLY, NOT_NULL, MIN, MAX, REGEXP, COLLATE);

    private final String className;

    private OPropertyPrototyper(String className) {
        this.className = className;
    }

    @Override
    protected OProperty createInstance(OProperty proxy) {
        OSchema schema = OrientDbWebSession.get().getDatabase().getMetadata().getSchema();
        OClass oClass = schema.getClass(className);
        return oClass.createProperty(proxy.getName(), proxy.getType());
    }

    @Override
    protected Object handleGet(String propName, Class<?> returnType) {
        if ("ownerClass".equals(propName)) {
            OSchema schema = OrientDbWebSession.get().getDatabase().getMetadata().getSchema();
            return schema.getClass(className);
        } else {
            return super.handleGet(propName, returnType);
        }
    }

    @Override
    protected Class<OProperty> getMainInterface() {
        return OProperty.class;
    }

    public static OProperty newPrototype(String className) {
        return newPrototype(new OPropertyPrototyper(className));
    }

}
