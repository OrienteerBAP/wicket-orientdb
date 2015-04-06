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
package ru.ydn.wicket.wicketorientdb.utils;

import java.io.Serializable;

import org.apache.wicket.model.IModel;

import com.google.common.base.Function;

/**
 * Utility {@link Function} for obtaining object from specified {@link IModel}
 *
 * @param <T>
 */
public class GetObjectFunction<T> implements Function<IModel<T>, T>, Serializable {

    private static final long serialVersionUID = 1L;
    public static final GetObjectFunction<?> INSTANCE = new GetObjectFunction<Object>();

    @Override
    public T apply(IModel<T> input) {
        return input.getObject();
    }

    @SuppressWarnings("unchecked")
    public static <T> GetObjectFunction<T> getInstance() {
        return (GetObjectFunction<T>) INSTANCE;
    }

}
