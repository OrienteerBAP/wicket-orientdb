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
package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.index.OIndexDefinition;
import com.orientechnologies.orient.core.index.OIndexManager;
import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * Model for storing {@link OIndex}
 */
public class OIndexModel extends PrototypeLoadableDetachableModel<OIndex<?>> {

    private static final long serialVersionUID = 1L;
    private IModel<OClass> classModel;
    private String indexName;

    public OIndexModel(OIndex<?> object) {
        super(object);
    }

    public OIndexModel(String indexName) {
        this.indexName = indexName;
    }

    public OIndexModel(OClass oClass, String indexName) {
        this.classModel = new OClassModel(oClass);
        this.indexName = indexName;
    }

    @Override
    protected OIndex<?> loadInstance() {
        OClass oClass = classModel != null ? classModel.getObject() : null;
        OIndexManager indexManager = OrientDbWebSession.get().getDatabase().getMetadata().getIndexManager();
        return oClass != null ? indexManager.getClassIndex(oClass.getName(), indexName) : indexManager.getIndex(indexName);
    }

    @Override
    protected void handleObject(OIndex<?> object) {
        indexName = object.getName();
        OIndexDefinition indexDefinition = object.getDefinition();
        if (indexDefinition != null) {
            String className = indexDefinition.getClassName();
            if (className != null) {
                classModel = new OClassModel(className);
            }
        }
    }

    @Override
    public void detach() {
        super.detach();
        if (classModel != null) {
            classModel.detach();
        }
    }

}
