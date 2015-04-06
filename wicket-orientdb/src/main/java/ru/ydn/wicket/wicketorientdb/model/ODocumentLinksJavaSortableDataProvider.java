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

import java.util.Collection;

import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODocumentLinksJavaSortableDataProvider<S> extends
        AbstractJavaSortableDataProvider<ODocument, S> {

    public ODocumentLinksJavaSortableDataProvider(IModel<ODocument> docModel, IModel<OProperty> propertyModel) {
        this(new DynamicPropertyValueModel<Collection<ODocument>>(docModel, propertyModel));
    }

    public ODocumentLinksJavaSortableDataProvider(
            IModel<? extends Collection<ODocument>> dataModel) {
        super(dataModel);
    }

    @Override
    protected Comparable<?> comparableValue(ODocument input, S sortParam) {
        String property = getSortPropertyExpression(sortParam);
        if (property == null) {
            return null;
        }
        Object value = input.field(property);
        return value instanceof Comparable ? (Comparable<?>) value : null;
    }

    @Override
    public IModel<ODocument> model(ODocument object) {
        return new ODocumentModel(object);
    }

}
