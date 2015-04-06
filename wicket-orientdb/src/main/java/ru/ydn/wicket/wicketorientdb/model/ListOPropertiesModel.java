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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;

import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class ListOPropertiesModel extends AbstractListModel<OProperty> {

    private IModel<OClass> oClassModel;
    private IModel<Boolean> allPropertiesModel;

    public ListOPropertiesModel(final IModel<OClass> oClassModel, final IModel<Boolean> allPropertiesModel) {
        Args.notNull(oClassModel, "oClassModel");
        this.oClassModel = oClassModel;
        this.allPropertiesModel = allPropertiesModel;
    }

    @Override
    public Collection<OProperty> getData() {
        OClass oClass = oClassModel.getObject();
        if (oClass == null) {
            return null;
        } else if (allPropertiesModel == null || Boolean.TRUE.equals(allPropertiesModel.getObject())) {
            return oClass.properties();
        } else {
            return oClass.declaredProperties();
        }
    }

    @Override
    public void detach() {
        super.detach();
        if (allPropertiesModel != null) {
            allPropertiesModel.detach();
        }
        oClassModel.detach();
    }

}
