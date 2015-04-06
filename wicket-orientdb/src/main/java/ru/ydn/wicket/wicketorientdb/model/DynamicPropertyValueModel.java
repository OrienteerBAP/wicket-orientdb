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
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.lang.Args;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class DynamicPropertyValueModel<T> extends LoadableDetachableModel<T> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    protected final IModel<ODocument> docModel;
    protected final IModel<OProperty> propertyModel;

    public DynamicPropertyValueModel(IModel<ODocument> docModel, IModel<OProperty> propertyModel) {
        Args.notNull(docModel, "documentModel");
        this.docModel = docModel;
        this.propertyModel = propertyModel;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T load() {
        ODocument doc = docModel.getObject();
        OProperty prop = propertyModel != null ? propertyModel.getObject() : null;
        if (doc == null) {
            return null;
        }
        if (prop == null) {
            return (T) doc;
        }
        return (T) doc.field(prop.getName());
    }

    @Override
    protected void onDetach() {
        docModel.detach();
        if (propertyModel != null) {
            propertyModel.detach();
        }
    }

    @Override
    public void setObject(T object) {
        ODocument doc = docModel.getObject();
        OProperty prop = propertyModel != null ? propertyModel.getObject() : null;
        if (doc == null) {
            return;
        }
        if (prop == null) {
            if (object instanceof OIdentifiable) {
                docModel.setObject((ODocument) object);
            }
        } else {
            doc.field(prop.getName(), object);
        }
        super.setObject(object);
    }

}
