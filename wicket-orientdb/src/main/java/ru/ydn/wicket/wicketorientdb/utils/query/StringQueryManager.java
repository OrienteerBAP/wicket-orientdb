package ru.ydn.wicket.wicketorientdb.utils.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.io.IClusterable;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.DefaultFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteria;

/**
 * String based query manager 
 */
public class StringQueryManager implements IQueryManager, IClusterable {
	
	private static final Pattern PROJECTION_PATTERN = Pattern.compile("select\\b(.+?)\\bfrom\\b", Pattern.CASE_INSENSITIVE);
	private static final Pattern EXPAND_PATTERN = Pattern.compile("expand\\((.+)\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern ORDER_CHECK_PATTERN = Pattern.compile("order\\s+by", Pattern.CASE_INSENSITIVE);
    
    private String projection;
    private boolean containExpand;
    private boolean hasOrderBy;
    private String sql;
    private String countSql;
    private IFilterCriteria filterCriteria;
    
    public StringQueryManager(String sql) {
    	this.sql = sql;
    	Matcher matcher = PROJECTION_PATTERN.matcher(sql);
        if(matcher.find())
        {
        	projection = matcher.group(1).trim();
        	Matcher expandMatcher = EXPAND_PATTERN.matcher(projection);
        	containExpand = expandMatcher.find();
        	if(containExpand)
        	{
        		countSql = matcher.replaceFirst("select sum("+expandMatcher.group(1)+".size()) as count from");
        	}
        	else
        	{
        		countSql = matcher.replaceFirst("select count(*) from"); 
        	}
        }
        else
        {
            throw new WicketRuntimeException("Can't find 'object(<.>)' part in your request: "+sql);
        }
        hasOrderBy = ORDER_CHECK_PATTERN.matcher(sql).find();
        filterCriteria = new DefaultFilterCriteria();
    }

	@Override
	public String getProjection() {
		return projection;
	}

	@Override
	public String getSql() {
		return sql;
	}

	@Override
	public String getCountSql() {
		return countSql;
	}

	@Override
	public boolean hasOrderBy() {
		return hasOrderBy;
	}

	@Override
	public String prepareSql(Integer first, Integer count, String sortBy, boolean isAscending) {
		String sql = getSql();
    	if (filterCriteria != null) {
			sql = filterCriteria.apply(sql);
		}
		boolean wrapForSkip = containExpand && first != null;
		StringBuilder sb = new StringBuilder(2 * sql.length());
		if (wrapForSkip) sb.append("select from (");
		sb.append(sql);
		if (sortBy != null) sb.append(" ORDER BY " + sortBy + (isAscending ? "" : " desc"));
		if (wrapForSkip) sb.append(") ");
		if (first != null) sb.append(" SKIP " + first);
		if (count != null && count > 0) sb.append(" LIMIT " + count);
		return sb.toString();
	}

	@Override
	public void setFilterCriteria(IFilterCriteria filterCriteria) {
		this.filterCriteria = filterCriteria;
	}

	@Override
	public IFilterCriteria getFilterCriteria() {
		return filterCriteria;
	}

}
