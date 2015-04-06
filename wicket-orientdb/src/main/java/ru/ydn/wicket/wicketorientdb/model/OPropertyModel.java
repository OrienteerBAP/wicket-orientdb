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

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

/**
 * Model for storing {@link OProperty} instance
 */
public class OPropertyModel extends PrototypeLoadableDetachableModel<OProperty> {

    private static final long serialVersionUID = 1L;
    private IModel<OClass> classModel;
    private String propertyName;

    public OPropertyModel(OProperty oProperty) {
        super(oProperty);
    }

    public OPropertyModel(String className, String propertyName) {
        this(new OClassModel(className), propertyName);
    }

    public OPropertyModel(IModel<OClass> classModel, String propertyName) {
        this.classModel = classModel;
        this.propertyName = propertyName;
    }

    @Override
    protected OProperty loadInstance() {
        OClass oClass = classModel != null ? classModel.getObject() : null;
        return oClass != null && propertyName != null ? oClass.getProperty(propertyName) : null;
    }

    @Override
    protected void handleObject(OProperty object) {
        if (object != null) {
            classModel = new OClassModel(object.getOwnerClass());
            propertyName = object.getName();
        } else {
            classModel = null;
            propertyName = null;
        }
    }

    @Override
    protected void onDetach() {
        if (classModel != null) {
            OProperty property = getObject();
            if (property != null && !property.getName().equals(propertyName)) {
                propertyName = property.getName();
            }
            classModel.detach();
        }
    }

    public OSchema getSchema() {
        return getDatabase().getMetadata().getSchema();
    }

    public ODatabaseDocument getDatabase() {
        return OrientDbWebSession.get().getDatabase();
    }

}
