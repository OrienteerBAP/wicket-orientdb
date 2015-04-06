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

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

/**
 * {@link SortableDataProvider} for listing of {@link OClass}es
 */
public class OClassesDataProvider extends AbstractJavaSortableDataProvider<OClass, String> {

    private static final long serialVersionUID = 1L;

    public OClassesDataProvider() {
        super(new ListOClassesModel());
    }

    public OClassesDataProvider(IModel<Collection<OClass>> dataModel) {
        super(dataModel);
    }

    @Override
    public IModel<OClass> model(OClass object) {
        return new OClassModel(object);
    }

    protected static OSchema getSchema() {
        return OrientDbWebSession.get().getDatabase().getMetadata().getSchema();
    }

    @Override
    protected String getSortPropertyExpression(String param) {
        if (OClassPrototyper.SUPER_CLASS.equals(param)) {
            return param + ".name";
        } else {
            return super.getSortPropertyExpression(param);
        }
    }

}
