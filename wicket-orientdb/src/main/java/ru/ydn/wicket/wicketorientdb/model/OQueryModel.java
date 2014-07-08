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
import ru.ydn.wicket.wicketorientdb.utils.GetObjectFunction;

import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

public class OQueryModel<K> extends LoadableDetachableModel<List<K>>
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Pattern PROJECTION_PATTERN = Pattern.compile("select\\b(.+?)\\bfrom\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern ORDER_CHECK_PATTERN = Pattern.compile("order\\s+by", Pattern.CASE_INSENSITIVE);

    private String sql;
    private String projection;
    private String countSql;
    private Map<String, IModel<Object>> params = new HashMap<String, IModel<Object>>();
    private String sortableParameter=null;
    private boolean isAccessing=true;
    
    private transient Long size;

    public OQueryModel(String sql)
    {
        this.sql=sql;
        Matcher matcher = PROJECTION_PATTERN.matcher(sql);
        if(matcher.find())
        {
        	projection = matcher.group(1).trim();
        	countSql = matcher.replaceAll("select count(*) from"); 
        }
        else
        {
            throw new WicketRuntimeException("Can't find 'object(<.>)' part in your request: "+sql);
        }
        if(ORDER_CHECK_PATTERN.matcher(sql).find())
        {
            throw new WicketRuntimeException(OQueryModel.class.getSimpleName()+" doesn't support 'order by' in supplied sql");
        }
    }

    public OQueryModel<K> setParameter(String paramName, IModel<Object> value)
    {
        params.put(paramName, value);
        super.detach();
        return this;
    }

    protected List<K> load()
    {
    	ODatabaseRecord db = OrientDbWebSession.get().getDatabase();
    	OSQLSynchQuery<K> query = new OSQLSynchQuery<K>(prepareSql(null, null));
    	return db.query(query, prepareParams());
    }

    @SuppressWarnings("unchecked")
	public Iterator<K> iterator(long first, long count)
    {
    	ODatabaseRecord db = OrientDbWebSession.get().getDatabase();
    	OSQLSynchQuery<K> query = new OSQLSynchQuery<K>(prepareSql((int)first, (int)count));
    	return ((List<K>)db.query(query, prepareParams())).iterator();
    }
    
    protected String prepareSql(Integer first, Integer count)
    {
    	StringBuilder sb = new StringBuilder(sql);
    	if(first!=null) sb.append(" SKIP "+first);
    	if(count!=null && count>0) sb.append(" LIMIT "+count);
    	if(sortableParameter!=null) sb.append(" ORDER BY "+sortableParameter+(isAccessing?"":" desc"));
    	return sb.toString();
    }

    public String getCountSql()
    {
        return countSql;
    }


    public long size()
    {
    	if(size==null)
    	{
	    	ODatabaseRecord db = OrientDbWebSession.get().getDatabase();
	    	OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(getCountSql());
	    	List<ODocument> ret = db.query(query, prepareParams());
	    	size = ret.get(0).field("count");
    	}
    	return size;
    }

    private Map<String, Object> prepareParams()
    {
    	return Maps.transformValues(params, GetObjectFunction.getInstance());
    }


    public boolean isAccessing()
    {
        return isAccessing;
    }

    public OQueryModel<K> setAccessing(boolean accessing)
    {
        isAccessing = accessing;
        super.detach();
        return this;
    }

    public String getSortableParameter()
    {
        return sortableParameter;
    }

    public OQueryModel<K> setSortableParameter(String sortableParameter)
    {
        this.sortableParameter = sortableParameter;
        super.detach();
        return this;
    }
    
    public OQueryModel<K> setSort(String sortableParameter, SortOrder order)
    {
    	setSortableParameter(sortableParameter);
    	setAccessing(SortOrder.ASCENDING.equals(order));
    	return this;
    }
    
    public String getProjection() {
		return projection;
	}

	public void detach()
    {
        for (IModel<?> model : params.values())
        {
            model.detach();
        }
        super.detach();
        size=null;
    }
    
    public ODatabaseRecord getDatabase()
	{
		return OrientDbWebSession.get().getDatabase();
	}

}