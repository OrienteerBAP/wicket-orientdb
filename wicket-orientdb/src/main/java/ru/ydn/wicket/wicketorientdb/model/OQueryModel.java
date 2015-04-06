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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.utils.ConvertToODocumentFunction;
import ru.ydn.wicket.wicketorientdb.utils.DocumentWrapperTransformer;
import ru.ydn.wicket.wicketorientdb.utils.GetObjectFunction;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * Model to obtain data from OrientDB by query
 *
 * @param <K>
 */
public class OQueryModel<K> extends LoadableDetachableModel<List<K>> {

    private static class GetObjectAndWrapDocumentsFunction<T> extends GetObjectFunction<T> {

        private static final long serialVersionUID = 1L;
        public static final GetObjectAndWrapDocumentsFunction<?> INSTANCE = new GetObjectAndWrapDocumentsFunction<Object>();

        @Override
        @SuppressWarnings("unchecked")
        public T apply(IModel<T> input) {
            T ret = super.apply(input);
            if (ret instanceof ORecord) {
                ret = (T) ((ORecord) ret).getIdentity();
            }
            return ret;
        }

        @SuppressWarnings("unchecked")
        public static <T> GetObjectAndWrapDocumentsFunction<T> getInstance() {
            return (GetObjectAndWrapDocumentsFunction<T>) INSTANCE;
        }
    }
    private static final long serialVersionUID = 1L;
    private static final Pattern PROJECTION_PATTERN = Pattern.compile("select\\b(.+?)\\bfrom\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern EXPAND_PATTERN = Pattern.compile("expand\\((.+)\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern ORDER_CHECK_PATTERN = Pattern.compile("order\\s+by", Pattern.CASE_INSENSITIVE);

    private String sql;
    private Function<?, K> transformer;
    private String projection;
    private String countSql;
    private Map<String, IModel<Object>> params = new HashMap<String, IModel<Object>>();
    private String sortableParameter = null;
    private boolean isAccessing = true;

    private transient Long size;

    /**
     * @param sql SQL to be executed to obtain data
     */
    public OQueryModel(String sql) {
        this(sql, (Function<?, K>) null);
    }

    /**
     * @param sql SQL to be executed to obtain data
     * @param wrapperClass target type for wrapping of {@link ODocument}
     */
    public OQueryModel(String sql, Class<? extends K> wrapperClass) {
        this(sql, new DocumentWrapperTransformer<K>(wrapperClass));
    }

    /**
     * @param sql SQL to be executed to obtain data
     * @param transformer transformer for wrapping of {@link ODocument} ot
     * required type
     */
    @SuppressWarnings("unchecked")
    public OQueryModel(String sql, Function<?, K> transformer) {
        this.sql = sql;
        this.transformer = transformer != null ? transformer : (Function<?, K>) ConvertToODocumentFunction.INSTANCE;
        Matcher matcher = PROJECTION_PATTERN.matcher(sql);
        if (matcher.find()) {
            projection = matcher.group(1).trim();
            Matcher expandMatcher = EXPAND_PATTERN.matcher(projection);
            if (expandMatcher.find()) {
                countSql = matcher.replaceFirst("select sum(" + expandMatcher.group(1) + ".size()) as count from");
            } else {
                countSql = matcher.replaceFirst("select count(*) from");
            }
        } else {
            throw new WicketRuntimeException("Can't find 'object(<.>)' part in your request: " + sql);
        }
        if (ORDER_CHECK_PATTERN.matcher(sql).find()) {
            throw new WicketRuntimeException(OQueryModel.class.getSimpleName() + " doesn't support 'order by' in supplied sql");
        }
    }

    /**
     * Set value for named parameter
     *
     * @param paramName name of the parameter to set
     * @param value {@link IModel} for the parameter value
     * @return
     */
    @SuppressWarnings("unchecked")
    public OQueryModel<K> setParameter(String paramName, IModel<?> value) {
        params.put(paramName, (IModel<Object>) value);
        super.detach();
        return this;
    }

    @SuppressWarnings("unchecked")
    protected List<K> load() {
        ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
        OSQLSynchQuery<K> query = new OSQLSynchQuery<K>(prepareSql(null, null));
        List<?> ret = db.query(query, prepareParams());

        return transformer == null ? (List<K>) ret : Lists.transform(ret, (Function<Object, K>) transformer);
    }

    /**
     * Get resulta as {@link Iterator}&lt;K&gt;. Suitable for pagination
     *
     * @param first
     * @param count
     * @return
     */
    @SuppressWarnings("unchecked")
    public Iterator<K> iterator(long first, long count) {
        ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
        OSQLSynchQuery<K> query = new OSQLSynchQuery<K>(prepareSql((int) first, (int) count));
        Iterator<?> iterator = db.query(query, prepareParams()).iterator();
        return transformer == null ? (Iterator<K>) iterator : Iterators.transform(iterator, (Function<Object, K>) transformer);
    }

    protected String prepareSql(Integer first, Integer count) {
        StringBuilder sb = new StringBuilder(getSql());
        if (first != null) {
            sb.append(" SKIP " + first);
        }
        if (count != null && count > 0) {
            sb.append(" LIMIT " + count);
        }
        if (sortableParameter != null) {
            sb.append(" ORDER BY " + sortableParameter + (isAccessing ? "" : " desc"));
        }
        return sb.toString();
    }

    protected String getSql() {
        return sql;
    }

    protected String getCountSql() {
        return countSql;
    }

    /**
     * Get the size of the data
     *
     * @return
     */
    public long size() {
        if (size == null) {
            ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
            OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(getCountSql());
            List<ODocument> ret = db.query(query, prepareParams());
            if (ret != null && ret.size() > 0) {
                Number sizeNumber = ret.get(0).field("count");
                size = sizeNumber.longValue();
            } else {
                size = 0L;
            }
        }
        return size;
    }

    private Map<String, Object> prepareParams() {
        //return Maps.transformValues(params, GetObjectFunction.getInstance());
        return Maps.transformValues(params, GetObjectAndWrapDocumentsFunction.getInstance());
    }

    /**
     * Is data shold be in accessing order?
     *
     * @return
     */
    public boolean isAccessing() {
        return isAccessing;
    }

    /**
     * Set order
     *
     * @param accessing
     * @return
     */
    public OQueryModel<K> setAccessing(boolean accessing) {
        isAccessing = accessing;
        super.detach();
        return this;
    }

    /**
     * @return paramer to sort on
     */
    public String getSortableParameter() {
        return sortableParameter;
    }

    /**
     * Set sortable parameter
     *
     * @param sortableParameter
     * @return
     */
    public OQueryModel<K> setSortableParameter(String sortableParameter) {
        this.sortableParameter = sortableParameter;
        super.detach();
        return this;
    }

    /**
     * Set sorting configration
     *
     * @param sortableParameter
     * @param order
     * @return
     */
    public OQueryModel<K> setSort(String sortableParameter, SortOrder order) {
        setSortableParameter(sortableParameter);
        setAccessing(SortOrder.ASCENDING.equals(order));
        return this;
    }

    /**
     * @return projection of the query
     */
    public String getProjection() {
        return projection;
    }

    @Override
    public void detach() {
        for (IModel<?> model : params.values()) {
            model.detach();
        }
        super.detach();
        size = null;
    }

    /**
     * @return Current {@link ODatabaseRecord}
     */
    public ODatabaseDocument getDatabase() {
        return OrientDbWebSession.get().getDatabase();
    }

}
