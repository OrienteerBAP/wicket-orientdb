package ru.ydn.wicket.wicketorientdb.model;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.utils.ConvertToODocumentFunction;
import ru.ydn.wicket.wicketorientdb.utils.DocumentWrapperTransformer;
import ru.ydn.wicket.wicketorientdb.utils.GetObjectFunction;
import ru.ydn.wicket.wicketorientdb.utils.OSchemaUtils;
import ru.ydn.wicket.wicketorientdb.utils.query.IQueryManager;
import ru.ydn.wicket.wicketorientdb.utils.query.StringQueryManager;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.FilterCriteriaType;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Model to obtain data from OrientDB by query
 * @param <K> The Model Object type
 */
public class OQueryModel<K> extends LoadableDetachableModel<List<K>>
{
	private static class GetObjectAndWrapDocumentsFunction<T> extends GetObjectFunction<T>
	{
		private static final long serialVersionUID = 1L;
		public static final GetObjectAndWrapDocumentsFunction<?> INSTANCE = new GetObjectAndWrapDocumentsFunction<Object>();
		
		@Override
		@SuppressWarnings("unchecked")
		public T apply(IModel<T> input) {
			T ret = super.apply(input);
			if(ret instanceof ORecord)
			{
				ret = (T)((ORecord)ret).getIdentity();
			}
			return ret;
		}
		
		@SuppressWarnings("unchecked")
		public static <T> GetObjectAndWrapDocumentsFunction<T> getInstance()
		{
			return (GetObjectAndWrapDocumentsFunction<T>)INSTANCE;
		}
	}
	private static final long serialVersionUID = 1L;

	private IQueryManager queryManager;
    private Function<OElement, K> transformer;
    private Map<String, IModel<Object>> params = new HashMap<String, IModel<Object>>();
    private Map<String, IModel<Object>> variables = new HashMap<String, IModel<Object>>();
    private String sortableParameter=null;
    private boolean isAscending =true;
    private boolean containExpand=true;
    
    private transient Long size;
    
    /**
	 * @param sql SQL to be executed to obtain data
	 */
    public OQueryModel(String sql)
    {
    	this(sql, (Function<?, K>) null);
    }
    
    /**
	 * @param sql SQL to be executed to obtain data
	 * @param wrapperClass target type for wrapping of {@link ODocument}
	 */
    public OQueryModel(String sql, Class<? extends K> wrapperClass)
    {
    	this(sql, new DocumentWrapperTransformer<K>(wrapperClass));
    }

    /**
	 * @param sql SQL to be executed to obtain data
	 * @param transformer transformer for wrapping of {@link ODocument} ot required type
	 */
    @SuppressWarnings("unchecked")
	public OQueryModel(String sql, Function<?, K> transformer)
    {
    	this.queryManager = new StringQueryManager(sql);
        this.transformer = transformer!=null?transformer:(Function<?, K>)ConvertToODocumentFunction.INSTANCE;
        if(queryManager.hasOrderBy())
        {
            throw new WicketRuntimeException(OQueryModel.class.getSimpleName()+" doesn't support 'order by' in supplied sql");
        }
    }

    /**
     * Set value for named parameter
     * @param paramName name of the parameter to set
     * @param value {@link IModel} for the parameter value
     * @return this {@link OQueryModel}
     */
    @SuppressWarnings("unchecked")
	public OQueryModel<K> setParameter(String paramName, IModel<?> value)
    {
        params.put(paramName, (IModel<Object>)value);
        super.detach();
        return this;
    }


    /**
     * Add filter for {@link OQueryModel}
     * @param field {@link String} filtered field
     * @param manager {@link IFilterCriteriaManager} filter manager
     */
    public void addFilterCriteriaManager(String field, IFilterCriteriaManager manager) {
        queryManager.addFilterCriteriaManager(field, manager);
    }

    /**
     * Add query parameters from {@link IFilterCriteriaManager}
     * @param manager {@link IFilterCriteriaManager} for filtering
     */
    private void addQueryParametersFromManager(IFilterCriteriaManager manager) {
        Map<FilterCriteriaType, IFilterCriteria> criterias = manager.getFilterCriterias();
        for (FilterCriteriaType type : criterias.keySet()) {
            IFilterCriteria criteria = criterias.get(type);
            if (criteria == null) continue;
            if (criteria.getModel().getObject() != null) {
                if (type.equals(FilterCriteriaType.RANGE)) {
                    List<?> list = (List<?>) criteria.getModel().getObject();
                    Object first = list.get(0);
                    Object second = list.get(1);
                    if (first != null && second != null) {
                        setParameter(criteria.getName() + 0, Model.of((Serializable) first));
                        setParameter(criteria.getName() + 1, Model.of((Serializable) second));
                    } else setParameter(criteria.getName(), Model.of(first != null ? (Serializable) first : (Serializable) second));
                } else setParameter(criteria.getName(), criteria.getModel());
            }
        }
    }

    /**
     * Get filter from {@link OQueryModel} by field name
     * @param field {@link String} filtered field
     * @return {@link IFilterCriteriaManager} or null if no filter for field
     */
    public IFilterCriteriaManager getFilterCriteriaManager(String field) {
        return queryManager.getFilterCriteriaManager(field);
    }

    /**
     * Remove all filter criteria managers for current {@link OQueryModel}
     */
    public void clearFilterCriteriaManagers() {
        queryManager.clearFilterCriteriaManagers();
    }

    /**
     * Set value for context variable
     * @param varName name of the variable to set
     * @param value {@link IModel} for the variable value
     * @return this {@link OQueryModel}
     */
    @SuppressWarnings("unchecked")
	public OQueryModel<K> setContextVariable(String varName, IModel<?> value)
    {
        variables.put(varName, (IModel<Object>)value);
        super.detach();
        return this;
    }

//    protected Map<String, Object> createQueryVariablesMap() {
//        return variables.entrySet().stream()
//                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getObject()));
//    }

	@SuppressWarnings("unchecked")
	protected List<K> load() {
    	ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
        String sql = prepareSql(null, null);
        OResultSet result = db.query(sql, createQueryVariablesMap());

        if (transformer != null) {
            return result.elementStream()
                    .map(transformer)
                    .collect(Collectors.toCollection(LinkedList::new));
        }

        return result.elementStream()
                .map(e -> (K) e)
                .collect(Collectors.toCollection(LinkedList::new));
    }

	/**
	 * Get result as {@link Iterator}&lt;K&gt;. Suitable for pagination
	 * @param first first element to start from
	 * @param count maximum size of a result set
	 * @return {@link Iterator} over results
	 */
    @SuppressWarnings("unchecked")
	public Iterator<K> iterator(long first, long count)
    {
    	return iterator(first, count, (Function<OElement, K>) transformer);
    }
    
    /**
	 * Get result as {@link Iterator}&lt;K&gt;. Suitable for pagination
	 * @param <T> type of objects to return
	 * @param first first element to start from
	 * @param count maximum size of a result set
	 * @param transformer transformer to use for results
	 * @return {@link Iterator} over results
	 */
    @SuppressWarnings("unchecked")
	public <T> Iterator<T> iterator(long first, long count, Function<OElement, T> transformer) {
    	ODatabaseDocument db = OrientDbWebSession.get().getDatabase();

        Map<String, Object> params = new HashMap<>(createQueryVariablesMap());
        params.putAll(prepareParams());

        OResultSet result = db.query(prepareSql((int) first, (int) count), params);

        if (transformer != null) {
            return result.elementStream()
                    .map(transformer)
                    .iterator();
        }

    	return result.elementStream()
                .map(e -> (T) e)
                .iterator();
    }
    
    /**
     * Probe the dataset and returns upper OClass for sample
     * @param probeLimit size of a probe
     * @return OClass or null if there is no common parent for {@link ODocument}'s in a sample
     */
    public OClass probeOClass(int probeLimit) {
    	Iterator<ODocument> it = iterator(0, probeLimit, null);
    	return OSchemaUtils.probeOClass(it, probeLimit);
    }
    
    protected String prepareSql(Integer first, Integer count)
    {
    	return queryManager.prepareSql(first, count, sortableParameter, isAscending);
    }
    
    /**
     * Get the size of the data
     * @return results size
     */
    public long size()
    {
    	if(size==null)
    	{
	    	ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
	    	OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(queryManager.getCountSql());
	    	List<ODocument> ret = db.query(enhanceContextByVariables(query), prepareParams());
	    	if(ret!=null && ret.size()>0)
	    	{
	    		Number sizeNumber = ret.get(0).field("count");
	    		size = sizeNumber!=null?sizeNumber.longValue():0;
	    	}
	    	else
	    	{
	    		size = 0L;
	    	}
    	}
    	return size;
    }

    private Map<String, Object> prepareParams() {
        for (IFilterCriteriaManager manager : queryManager.getFilterCriteriaManagers()) {
            addQueryParametersFromManager(manager);
        }
    	return Maps.transformValues(params, GetObjectAndWrapDocumentsFunction.getInstance());
    }


    /**
     * Is data shold be in ascending order?
     * @return true if sort is ascending
     */
    public boolean isAscending()
    {
        return isAscending;
    }

    /**
     * Set order
     * @param ascending true - for ascending, false - descessing
     * @return this {@link OQueryModel}
     */
    public OQueryModel<K> setAscending(boolean ascending)
    {
        isAscending = ascending;
        super.detach();
        return this;
    }

    /**
     * @return paramer to sort on
     */
    public String getSortableParameter()
    {
        return sortableParameter;
    }

    /**
     * Set sortable parameter
     * @param sortableParameter sortable parameter to sort on
     * @return this {@link OQueryModel}
     */
    public OQueryModel<K> setSortableParameter(String sortableParameter)
    {
        this.sortableParameter = sortableParameter;
        super.detach();
        return this;
    }
    
    /**
     * Set sorting configration
     * @param sortableParameter sortable parameter to sort on
     * @param order {@link SortOrder} to sort on
     * @return this {@link OQueryModel}
     */
    public OQueryModel<K> setSort(String sortableParameter, SortOrder order)
    {
    	setSortableParameter(sortableParameter);
    	setAscending(SortOrder.ASCENDING.equals(order));
    	return this;
    }
    
    @Override
	public void detach()
    {
        for (IModel<?> model : params.values())
        {
            model.detach();
        }
        super.detach();
        size=null;
    }
    
    /**
     * @return Current {@link ODatabaseDocument}
     */
    public ODatabaseDocument getDatabase()
	{
		return OrientDbWebSession.get().getDatabase();
	}

}