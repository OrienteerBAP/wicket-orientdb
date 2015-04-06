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
import org.apache.wicket.model.Model;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

/**
 * Model for storing {@link OClass} instance
 */
public class OClassModel extends PrototypeLoadableDetachableModel<OClass> {

    private static final long serialVersionUID = 1L;
    private IModel<String> classNameModel;

    public OClassModel(OClass oClass) {
        super(oClass);
    }

    public OClassModel(String className) {
        this.classNameModel = Model.of(className);
    }

    public OClassModel(IModel<String> classNameModel) {
        this.classNameModel = classNameModel;
    }

    @Override
    protected OClass loadInstance() {
        String className = classNameModel != null ? classNameModel.getObject() : null;
        return className != null ? getSchema().getClass(className) : null;
    }

    @Override
    protected void handleObject(OClass object) {
        String name = object != null ? object.getName() : null;
        if (classNameModel != null) {
            classNameModel.setObject(name);
        } else {
            classNameModel = Model.of(name);
        }
    }

    @Override
    protected void onDetach() {
        if (classNameModel != null) {
            OClass thisClass = getObject();
            if (thisClass != null && !thisClass.getName().equals(classNameModel.getObject())) {
                classNameModel.setObject(thisClass.getName());
            }
            classNameModel.detach();
        }
    }

    public OSchema getSchema() {
        return getDatabase().getMetadata().getSchema();
    }

    public ODatabaseDocument getDatabase() {
        return OrientDbWebSession.get().getDatabase();
    }

}
