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

import org.apache.wicket.model.Model;

import com.orientechnologies.orient.core.type.ODocumentWrapper;

public class ODocumentWrapperModel<T extends ODocumentWrapper> extends Model<T> {

    private static final long serialVersionUID = 1L;

    private boolean needToReload = false;

    public ODocumentWrapperModel() {
        super();
    }

    public ODocumentWrapperModel(T object) {
        super(object);
        needToReload = false;
    }

    @Override
    public T getObject() {
        T ret = super.getObject();
        if (ret != null && needToReload) {
            ret.load();
            needToReload = false;
        }
        return ret;
    }

    @Override
    public void setObject(T object) {
        super.setObject(object);
        needToReload = false;
    }

    @Override
    public void detach() {
        needToReload = true;
        super.detach();
    }

}
