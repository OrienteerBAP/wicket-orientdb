package ru.ydn.wicket.wicketorientdb.utils.query;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteria;

/**
 * Interface for different implementation of query managers 
 */
public interface IQueryManager {
	public String getProjection();
	public String getSql();
	public String getCountSql();
	public boolean hasOrderBy();
	public String prepareSql(Integer first, Integer count, String sortBy, boolean isAscending);
	public void setFilterCriteria(IFilterCriteria filterCriteria);
	public IFilterCriteria getFilterCriteria();
}
