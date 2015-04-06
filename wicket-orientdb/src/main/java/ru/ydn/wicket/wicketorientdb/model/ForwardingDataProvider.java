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

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;

public abstract class ForwardingDataProvider<T, S> extends SortableDataProvider<T, S> {

    protected abstract SortableDataProvider<T, S> delegate();

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        return delegate().iterator(first, count);
    }

    @Override
    public long size() {
        return delegate().size();
    }

    @Override
    public void detach() {
        super.detach();
        delegate().detach();
    }

    @Override
    public IModel<T> model(T object) {
        return delegate().model(object);
    }

}
