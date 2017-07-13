package ru.ydn.wicket.wicketorientdb.utils.query;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final Map<String, IFilterCriteriaManager> managers;
    
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
        managers = Maps.newHashMap();
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
		StringBuilder sb = new StringBuilder(sql.length() * 2);
		boolean wrapForSkip = containExpand && first != null;
		if (wrapForSkip) sb.append("select from (");
		sb.append(sql);
		if (!managers.isEmpty()) {
			String filter = applyFilters();
			if (!Strings.isNullOrEmpty(filter)) {
				boolean containsWhere = sql.toUpperCase().contains("WHERE");
				if (containsWhere) sb.append(" AND(");
				else sb.append(" WHERE ");
				sb.append(filter);
				if (containsWhere) sb.append(")");
			}
		}
		if (sortBy != null) sb.append(" ORDER BY " + sortBy + (isAscending ? "" : " desc"));
		if (wrapForSkip) sb.append(") ");
		if (first != null) sb.append(" SKIP " + first);
		if (count != null && count > 0) sb.append(" LIMIT " + count);
		return sb.toString();
	}

	private String applyFilters() {
    	StringBuilder sb = new StringBuilder();
		int counter = 0;
		for (String key : managers.keySet()) {
			IFilterCriteriaManager manager = managers.get(key);
			if (manager != null) {
				String filter = manager.apply();
				if (!Strings.isNullOrEmpty(filter)) {
					if (counter > 0)
						sb.append(" AND ");
					sb.append(filter);
					counter++;
				}
			}
		}
		return sb.toString();
	}

	@Override
	public void addFilterCriteriaManager(String field, IFilterCriteriaManager manager) {
		managers.put(field, manager);
	}

	@Override
	public IFilterCriteriaManager getFilterCriteriaManager(String field) {
		return managers.get(field);
	}

	@Override
	public void clearFilterCriteriaManagers() {
		managers.clear();
	}

}
