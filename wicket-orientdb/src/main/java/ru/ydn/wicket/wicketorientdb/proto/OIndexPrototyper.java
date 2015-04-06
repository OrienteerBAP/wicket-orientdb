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

import org.apache.wicket.util.lang.Args;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.index.OIndexDefinition;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

/**
 * Prototyper for {@link OIndex}
 */
@SuppressWarnings("rawtypes")
public class OIndexPrototyper extends AbstractPrototyper<OIndex> {

    private static final long serialVersionUID = 1L;

    public static interface MakeNameAndTypeWritableFix {

        public void setName(String name);

        public void setType(String type);
    }
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String DEF = "definition";
    public static final String DEF_CLASS_NAME = "definition.className";
    public static final String DEF_FIELDS = "definition.fields";
    public static final String DEF_FILED_TO_INDEX = "definition.fieldsToIndex";
    public static final String DEF_COLLATE = "definition.collate";
    public static final String DEF_NULLS_IGNORED = "definition.nullValuesIgnored";
    public static final String SIZE = "size";
    public static final String KEY_SIZE = "keySize";

    public static final List<String> OINDEX_ATTRS = Arrays.asList(NAME, TYPE, DEF_CLASS_NAME, DEF_FIELDS, DEF_COLLATE, DEF_NULLS_IGNORED, SIZE, KEY_SIZE);
    public static final List<String> RW_ATTRS = Arrays.asList(DEF_COLLATE, DEF_NULLS_IGNORED);

    private static final Class<?>[] FIX_INTERFACES = new Class<?>[]{MakeNameAndTypeWritableFix.class};

    public OIndexPrototyper(String className, List<String> fields) {
        Args.notEmpty(fields, "fields");
        values.put(DEF_CLASS_NAME, className);
        values.put(DEF_FIELDS, fields);
    }

    @Override
    protected OIndex<?> createInstance(OIndex proxy) {
        OSchema schema = OrientDbWebSession.get().getDatabase().getMetadata().getSchema();
        OClass oClass = schema.getClass(proxy.getDefinition().getClassName());
        String name = proxy.getName();
        List<String> fields = proxy.getDefinition().getFields();
        String type = proxy.getType();
        if (name == null) {
            name = oClass.getName() + "." + fields.get(0);
        }
        values.keySet().retainAll(RW_ATTRS);
        return oClass.createIndex(name, type, fields.toArray(new String[0]));
    }

    @Override
    protected Object getDefaultValue(String propName, Class<?> returnType) {
        if (DEF.equals(propName)) {
            return prototypeForChild(propName, OIndexDefinition.class);
        } else {
            return super.getDefaultValue(propName, returnType);
        }
    }

    @Override
    protected Class<OIndex> getMainInterface() {
        return OIndex.class;
    }

    public static OIndex<?> newPrototype(String className, List<String> fields) {
        return newPrototype(new OIndexPrototyper(className, fields));
    }

    @Override
    protected Class<?>[] getAdditionalInterfaces() {
        return FIX_INTERFACES;
    }

}
