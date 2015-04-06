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
package ru.ydn.wicket.wicketorientdb.components.table;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;

import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Simple implementation of {@link IColumn} for printing field value of a
 * {@link ODocument}
 */
public class DocumentPropertyColumn extends PropertyColumn<ODocument, String> {

    private static final long serialVersionUID = 1L;

    public DocumentPropertyColumn(IModel<String> displayModel,
            String sortProperty, String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    public DocumentPropertyColumn(IModel<String> displayModel,
            String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    @Override
    public IModel<Object> getDataModel(IModel<ODocument> rowModel) {
        ODocumentPropertyModel<Object> propertyModel = new ODocumentPropertyModel<Object>(rowModel, getPropertyExpression());
        return propertyModel;
    }

}
