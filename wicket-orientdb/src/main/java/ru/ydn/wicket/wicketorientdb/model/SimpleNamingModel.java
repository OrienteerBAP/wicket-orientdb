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

/**
 * Simple naming model which use {@link Object}.toString() for obtaining
 * resource key.
 *
 * @param <T>
 */
public class SimpleNamingModel<T> extends AbstractNamingModel<T> {

    private static final long serialVersionUID = 1L;
    private String prefix;

    public SimpleNamingModel(IModel<T> objectModel) {
        super(objectModel);
    }

    public SimpleNamingModel(T object) {
        super(object);
    }

    public SimpleNamingModel(String prefix, IModel<T> objectModel) {
        super(objectModel);
        this.prefix = prefix;
    }

    @Override
    public String getResourceKey(T object) {
        String objectStr = object != null ? object.toString() : "null";
        return prefix == null ? objectStr : prefix + "." + objectStr;
    }

}
