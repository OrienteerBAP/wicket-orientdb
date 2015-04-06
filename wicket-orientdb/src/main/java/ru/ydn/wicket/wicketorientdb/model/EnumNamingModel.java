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
import org.apache.wicket.util.lang.Classes;

/**
 * {@link AbstractNamingModel} for {@link Enum}s
 *
 * @param <T>
 */
public class EnumNamingModel<T extends Enum<T>> extends AbstractNamingModel<T> {

    private static final long serialVersionUID = 1L;

    public EnumNamingModel(IModel<T> objectModel, boolean dynamic) {
        super(objectModel, dynamic);
    }

    public EnumNamingModel(IModel<T> objectModel) {
        super(objectModel);
    }

    public EnumNamingModel(T object) {
        super(object);
    }

    @Override
    public String getResourceKey(T object) {
        return Classes.simpleName(object.getDeclaringClass()) + '.' + object.name();
    }

}
