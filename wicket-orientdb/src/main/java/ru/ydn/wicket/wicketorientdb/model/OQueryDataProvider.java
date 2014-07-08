package ru.ydn.wicket.wicketorientdb.model;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class OQueryDataProvider <K> extends SortableDataProvider<K, String>
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OQueryModel<K> model;

    public OQueryDataProvider(String sql)
    {
        model = new OQueryModel<K>(sql);
    }

    public OQueryDataProvider<K> setParameter(String paramName, IModel<Object> value)
    {
        model.setParameter(paramName, value);
        return this;
    }

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

    @SuppressWarnings("unchecked")
	public IModel<K> model(K o)
    {
    	if(o instanceof ODocument) return (IModel<K>)new ODocumentModel((ODocument)o);
    	else if(o instanceof Serializable) return (IModel<K>)Model.of((Serializable)o);
    	else throw new WicketRuntimeException(OQueryDataProvider.class.getSimpleName()+" can't work with non serializable objects: "+o);
    }

    public long size()
    {
        return model.size();        
    }

    public void detach()
    {
        model.detach();
        super.detach();        
    }
}