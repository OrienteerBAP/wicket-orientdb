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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.OClusterSelectionFactory;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.OClusterSelectionStrategy;

/**
 * Prototyper for {@link OClass}
 */
public class OClassPrototyper extends AbstractPrototyper<OClass> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "name";
    public static final String SHORT_NAME = "shortName";
    public static final String SUPER_CLASS = "superClass";
    public static final String OVER_SIZE = "overSize";
    public static final String STRICT_MODE = "strictMode";
    public static final String ABSTRACT = "abstract";
    public static final String CLUSTER_SELECTION = "clusterSelection";
    public static final String JAVA_CLASS = "javaClass";

    public static final List<String> OCLASS_ATTRS = Arrays.asList(NAME, SHORT_NAME, SUPER_CLASS, OVER_SIZE, STRICT_MODE, ABSTRACT, JAVA_CLASS, CLUSTER_SELECTION);

    private OClassPrototyper() {
        values.put("overSize", (float) 0);
    }

    @Override
    protected OClass createInstance(OClass proxy) {
        OSchema schema = OrientDbWebSession.get().getDatabase().getMetadata().getSchema();
        return schema.createClass(proxy.getName());
    }

    @Override
    protected Class<OClass> getMainInterface() {
        return OClass.class;
    }

    public static OClass newPrototype() {
        return newPrototype(new OClassPrototyper());
    }

    @Override
    protected Object handleSet(String propName, Object value) {
        if ("clusterSelection".equals(propName)) {
            if (value instanceof CharSequence) {
                value = new OClusterSelectionFactory().newInstance(value.toString());
            }
            if (value instanceof OClusterSelectionStrategy) {
                return super.handleSet(propName, value);
            } else {
                return null;
            }

        }
        //Default
        return super.handleSet(propName, value);
    }

    @Override
    protected Object handleCustom(Object proxy, Method method, Object[] args) {
        String methodName = method.getName();
        if ("properties".equals(methodName) || "declaredProperties".equals(methodName)) {
            return Collections.EMPTY_SET;
        } else {
            return super.handleCustom(proxy, method, args);
        }
    }

    @Override
    public String toString() {
        return "Prototype for '" + getMainInterface().getName() + "'";
    }

}
