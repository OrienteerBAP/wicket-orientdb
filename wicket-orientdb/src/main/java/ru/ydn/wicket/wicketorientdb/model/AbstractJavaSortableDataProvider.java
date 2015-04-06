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
import java.util.Iterator;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;

/**
 * Realization of {@link SortableDataProvider} which use properties values and
 * in memory sorting
 *
 * @param <T>
 * @param <S> the type of the sorting parameter
 */
public abstract class AbstractJavaSortableDataProvider<T, S> extends SortableDataProvider<T, S> {

    private static final long serialVersionUID = 1L;
    private IModel<? extends Collection<T>> dataModel;

    public AbstractJavaSortableDataProvider(IModel<? extends Collection<T>> dataModel) {
        Args.notNull(dataModel, "dataModel");
        this.dataModel = dataModel;
    }

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        Collection<T> data = dataModel.getObject();
        if (data == null || data.size() == 0) {
            return Iterators.emptyIterator();
        }
        Iterator<T> it;
        final SortParam<S> sortParam = getSort();
        if (sortParam != null && sortParam.getProperty() != null) {
            Ordering<T> ordering = Ordering.natural().nullsFirst().onResultOf(new Function<T, Comparable<?>>() {

                @Override
                public Comparable<?> apply(T input) {
                    return comparableValue(input, sortParam.getProperty());
                }
            });
            if (!sortParam.isAscending()) {
                ordering = ordering.reverse();
            }
            it = ordering.sortedCopy(data).iterator();
        } else {
            it = data.iterator();
        }
        if (first > 0) {
            Iterators.advance(it, (int) first);
        }
        return count >= 0 ? Iterators.limit(it, (int) count) : it;
    }

    protected Comparable<?> comparableValue(T input, S sortParam) {
        String property = getSortPropertyExpression(sortParam);
        if (property == null) {
            return null;
        }
        Object value = PropertyResolver.getValue(property, input);
        return value instanceof Comparable ? (Comparable<?>) value : null;
    }

    protected String getSortPropertyExpression(S param) {
        return param != null ? param.toString() : null;
    }

    @Override
    public long size() {
        Collection<T> data = dataModel.getObject();
        return data != null ? data.size() : 0;
    }

    @Override
    public void detach() {
        super.detach();
        dataModel.detach();
    }

}
