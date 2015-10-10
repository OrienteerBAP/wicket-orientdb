package ru.ydn.wicket.wicketorientdb.model;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.google.common.base.Function;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * Provider of data by quering of OrientDB
 * @param <K>
 */
public class OQueryDataProvider <K> extends SortableDataProvider<K, String>
{
	private static final long serialVersionUID = 1L;
	private OQueryModel<K> model;
	
	/**
	 * @param sql SQL to be executed to obtain data
	 */
	public OQueryDataProvider(String sql)
	{
		model = new OQueryModel<K>(sql);
	}
	/**
	 * @param sql SQL to be executed to obtain data
	 * @param transformer transformer for wrapping of {@link ODocument} ot required type
	 */
	public OQueryDataProvider(String sql, Function<?, K> transformer)
	{
		model = new OQueryModel<K>(sql, transformer);
	}
	/**
	 * @param sql SQL to be executed to obtain data
	 * @param wrapperClass target type for wrapping of {@link ODocument}
	 */
    public OQueryDataProvider(String sql, Class<? extends K> wrapperClass)
    {
        model = new OQueryModel<K>(sql, wrapperClass);
    }

    /**
     * Set value for named parameter
     * @param paramName name of the parameter to set
     * @param value {@link IModel} for the parameter value
     * @return
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
            model.setAccessing(sort.isAscending());
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
}