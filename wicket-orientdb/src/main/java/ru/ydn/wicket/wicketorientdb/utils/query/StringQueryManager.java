package ru.ydn.wicket.wicketorientdb.utils.query;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.wicket.WicketRuntimeException;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String based query manager 
 */
public class StringQueryManager implements IQueryManager {
	
	private static final Pattern PROJECTION_PATTERN = Pattern.compile("select\\b(.+?)\\bfrom\\b", Pattern.CASE_INSENSITIVE);
	private static final Pattern EXPAND_PATTERN = Pattern.compile("expand\\((.+)\\)", Pattern.CASE_INSENSITIVE);
    private static final Pattern ORDER_CHECK_PATTERN = Pattern.compile("order\\s+by", Pattern.CASE_INSENSITIVE);
    
    private String projection;
    private boolean containExpand;
    private boolean hasOrderBy;
    private String sql;
    private String countSql;
    private final Map<String, IFilterCriteriaManager> managers;
    private String linkListField;

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
		return sql + " " + getFilterCriteria(sql);
	}

	@Override
	public String getCountSql() {
		return countSql + " " + getFilterCriteria(countSql);
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
		if (sortBy != null) sb.append(" ORDER BY " + sortBy + (isAscending ? "" : " desc"));
		if (wrapForSkip) sb.append(") ");
		if (first != null) sb.append(" SKIP " + first);
		if (count != null && count > 0) sb.append(" LIMIT " + count);
		return sb.toString();
	}

	private String getFilterCriteria(String sql) {
    	StringBuilder sb = new StringBuilder();
		if (!managers.isEmpty()) {
			String filter = applyFilters();
			if (!Strings.isNullOrEmpty(filter)) {
				boolean containsWhere = sql.toUpperCase().contains("WHERE");
				sb.append(containsWhere ? " AND " : " WHERE ");
				if (!Strings.isNullOrEmpty(linkListField)) {
					sb.append(" :").append(linkListField).append(" CONTAINS(").append(filter).append(")");
				} else sb.append(filter);
			}
		}
		return sb.toString();
	}

	private String applyFilters() {
    	StringBuilder sb = new StringBuilder();
		int counter = 0;
		for (String key : managers.keySet()) {
			IFilterCriteriaManager manager = managers.get(key);
			if (manager != null) {
				if (manager.isFilterApply()) {
					if (counter > 0)
						sb.append(" AND ");
					sb.append(manager.apply());
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
	public Collection<IFilterCriteriaManager> getFilterCriteriaManagers() {
		return Collections.unmodifiableCollection(managers.values());
	}

	@Override
	public IFilterCriteriaManager getFilterCriteriaManager(String field) {
		return managers.get(field);
	}

	@Override
	public void clearFilterCriteriaManagers() {
		managers.clear();
	}

	@Override
	public void setLinkListField(String linkListField) {
		this.linkListField = linkListField;
	}

}
