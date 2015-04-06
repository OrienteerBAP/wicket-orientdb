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

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

/**
 * {@link SortableDataProvider} for listing of {@link OProperty}es of specific
 * {@link OClass}
 */
public class OPropertiesDataProvider
        extends
        AbstractJavaSortableDataProvider<OProperty, String> {

    private static final long serialVersionUID = 1L;

    public OPropertiesDataProvider(OClass oClass, boolean allProperties) {
        this(new OClassModel(oClass), Model.<Boolean>of(allProperties));
    }

    public OPropertiesDataProvider(final IModel<OClass> oClassModel, final IModel<Boolean> allPropertiesModel) {
        super(new ListOPropertiesModel(oClassModel, allPropertiesModel));
    }

    public OPropertiesDataProvider(IModel<Collection<OProperty>> dataModel) {
        super(dataModel);
    }

    @Override
    public IModel<OProperty> model(OProperty object) {
        return new OPropertyModel(object);
    }

}
