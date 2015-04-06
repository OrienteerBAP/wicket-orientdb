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

import org.apache.wicket.model.PropertyModel;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Model for obtaining and setting value of a {@link ODocument} property
 *
 * @param <T>
 */
public class ODocumentPropertyModel<T> extends PropertyModel<T> {

    private static final long serialVersionUID = 1L;

    public ODocumentPropertyModel(Object modelObject, String expression) {
        super(wrapDocumentIfRequired(modelObject), expression);
    }

    public static Object wrapDocumentIfRequired(Object obj) {
        return obj instanceof ODocument
                ? new ODocumentMapWrapper((ODocument) obj)
                : obj;
    }

    public ODocument getDocument() {
        Object target = getInnermostModelOrObject();
        if (target instanceof ODocument) {
            return (ODocument) target;
        } else if (target instanceof ODocumentMapWrapper) {
            return ((ODocumentMapWrapper) target).getDocument();
        } else {
            return null;
        }
    }

    @Override
    public T getObject() {
        ODocument doc = getDocument();
        String expression = getPropertyExpression();
        if (doc != null && (!expression.contains(".") || doc.isAllowChainedAccess())) {
            return doc.field(getPropertyExpression());
        } else {
            return super.getObject();
        }
    }

    @Override
    public void setObject(T object) {
        ODocument doc = getDocument();
        String expression = getPropertyExpression();
        if (doc != null && (!expression.contains(".") || doc.isAllowChainedAccess())) {
            doc.field(getPropertyExpression(), object);
        } else {
            super.setObject(object);
        }
    }

}
