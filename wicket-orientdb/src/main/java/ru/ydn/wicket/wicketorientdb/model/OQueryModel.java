package ru.ydn.wicket.wicketorientdb.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.utils.ConvertToODocumentFunction;
import ru.ydn.wicket.wicketorientdb.utils.DocumentWrapperTransformer;
import ru.ydn.wicket.wicketorientdb.utils.GetObjectFunction;
import ru.ydn.wicket.wicketorientdb.utils.OSchemaUtils;
import ru.ydn.wicket.wicketorientdb.utils.query.IQueryManager;
import ru.ydn.wicket.wicketorientdb.utils.query.StringQueryManager;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteria;

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
    private Function<?, K> transformer;
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
    	this(sql, (Function<?, K>)null);
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
     * Set {@link IFilterCriteria} for whis {@link OQueryModel<K>}
     * @param filterCriteria {@link IFilterCriteria} for filtering
     * @return this {@link OQueryModel<K>}
     */
    public OQueryModel<K> setFilterCriteria(IFilterCriteria filterCriteria) {
        queryManager.setFilterCriteria(filterCriteria);
        return this;
    }

    /**
     * @return current {@link IFilterCriteria}
     */
    public IFilterCriteria getFilterCriteria() {
        return queryManager.getFilterCriteria();
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
    
    protected <T> OSQLSynchQuery<T> enhanceContextByVariables(OSQLSynchQuery<T> query) {
    	for(Map.Entry<String, IModel<Object>> var: variables.entrySet()) {
    		query.getContext().setVariable(var.getKey(), var.getValue().getObject());
    	}
    	return query;
    }

	@SuppressWarnings("unchecked")
	protected List<K> load()
    {
    	ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
    	OSQLSynchQuery<K> query = new OSQLSynchQuery<K>(prepareSql(null, null));
    	List<?> ret = db.query(enhanceContextByVariables(query), prepareParams());
    	
    	return transformer==null?(List<K>)ret:Lists.transform(ret, (Function<Object, K>)transformer);
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
    	return iterator(first, count, (Function<Object, K>)transformer);
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
	public <T> Iterator<T> iterator(long first, long count, Function<Object, T> transformer)
    {
    	ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
    	OSQLSynchQuery<K> query = new OSQLSynchQuery<K>(prepareSql((int)first, (int)count));
    	Iterator<?> iterator = db.query(enhanceContextByVariables(query), prepareParams()).iterator();
    	return transformer==null?(Iterator<T>)iterator:Iterators.transform(iterator, transformer);
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

    private Map<String, Object> prepareParams()
    {
    	//return Maps.transformValues(params, GetObjectFunction.getInstance());
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