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

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODocumentLinksDataProvider extends ForwardingDataProvider<ODocument, String> {

    private IModel<ODocument> docModel;
    private IModel<OProperty> propertyModel;

    private ODocumentLinksJavaSortableDataProvider<String> javaSortableProvider;
    private ODocumentLinksQueryDataProvider queryProvider;

    private SortableDataProvider<ODocument, String> thisRunProvider;

    public ODocumentLinksDataProvider(IModel<ODocument> docModel, OProperty property) {
        this(docModel, new OPropertyModel(property));
    }

    public ODocumentLinksDataProvider(IModel<ODocument> docModel, IModel<OProperty> propertyModel) {
        this.docModel = docModel;
        this.propertyModel = propertyModel;
    }

    @Override
    public void detach() {
//        super.detach();
        propertyModel.detach();
        docModel.detach();
        if (thisRunProvider != null) {
            thisRunProvider.detach();
            thisRunProvider = null;
        }
    }

    @Override
    protected SortableDataProvider delegate() {
        if (thisRunProvider == null) {
            if (useQueryProvider()) {
                if (queryProvider == null) {
                    queryProvider = new ODocumentLinksQueryDataProvider(docModel, propertyModel);
                }
                thisRunProvider = queryProvider;
            } else {
                if (javaSortableProvider == null) {
                    javaSortableProvider = new ODocumentLinksJavaSortableDataProvider<String>(docModel, propertyModel);
                }
                thisRunProvider = javaSortableProvider;
            }
        }
        return thisRunProvider;
    }

    protected boolean useQueryProvider() {
        ODocument doc = docModel.getObject();
        return doc.getIdentity().isPersistent();
    }

    @Override
    public IModel<ODocument> model(ODocument object) {
        return new ODocumentModel(object);
    }
}
