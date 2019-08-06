package ru.ydn.wicket.wicketorientdb.model;

import com.google.common.base.Function;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;

import java.util.Iterator;

/**
 * Provider of data by quering of OrientDB
 * @param <K> The provider object type
 */
public class OQueryDataProvider <K> extends AbstractFilteredProvider<K, String> {
	private static final long serialVersionUID = 1L;
	private OQueryModel<K> model;

	/**
	 * @param sql SQL to be executed to obtain data
	 */
	public OQueryDataProvider(String sql)
	{
		this(new OQueryModel<K>(sql));
	}
	/**
	 * @param sql SQL to be executed to obtain data
	 * @param transformer transformer for wrapping of {@link ODocument} ot required type
	 */
	public OQueryDataProvider(String sql, Function<OElement, K> transformer)
	{
		this(new OQueryModel<K>(sql, transformer));
	}
	/**
	 * @param sql SQL to be executed to obtain data
	 * @param wrapperClass target type for wrapping of {@link ODocument}
	 */
    public OQueryDataProvider(String sql, Class<? extends K> wrapperClass)
    {
        this(new OQueryModel<K>(sql, wrapperClass));
    }
    
    /**
     * Low level constructor to initialize by direct {@link OQueryModel}
     * @param model {@link OQueryModel} to use in provider
     */
    public OQueryDataProvider(OQueryModel<K> model)
    {
    	this.model = model;
    }

    /**
     * Set value for named parameter
     * @param paramName name of the parameter to set
     * @param value {@link IModel} for the parameter value
     * @return this {@link OQueryDataProvider}
     */
    public OQueryDataProvider<K> setParameter(String paramName, IModel<?> value)
    {
        model.setParameter(paramName, value);
        return this;
    }

    @Override
    public Iterator<K> iterator(long first, long count)
    {
        SortParam<String> sort = getSort();
        if(sort!=null)
        {
            model.setSortableParameter(sort.getProperty());
            model.setAscending(sort.isAscending());
        }
        return (Iterator<K>)model.iterator(first, count);        
    }
    
    public OClass probeOClass(int probeLimit) {
    	return model.probeOClass(probeLimit);
    }

    @SuppressWarnings("unchecked")
	public IModel<K> model(K o)
    {
    	return ModelUtils.model(o);
    }
    
    /**
     * Set sort
     * @param property property to sort on
     * @param order order to apply: true is for ascending, false is for descending
     */
    public void setSort(String property, Boolean order) {
    	SortOrder sortOrder = order==null?SortOrder.ASCENDING:(order?SortOrder.ASCENDING:SortOrder.DESCENDING);
    	if(property==null) {
    		if(order==null) setSort(null);
    		else setSort("@rid", sortOrder);
    	} else {
    		super.setSort(property, sortOrder);
    	}
    }

    @Override
    public long size()
    {
        return model.size();
    }

    @Override
    public void detach()
    {
        model.detach();
        super.detach();        
    }

    @Override
    public OQueryModel<K> getFilterState() {
        return model;
    }

    @Override
    public boolean isFilterEnable() {
        return true;
    }
}