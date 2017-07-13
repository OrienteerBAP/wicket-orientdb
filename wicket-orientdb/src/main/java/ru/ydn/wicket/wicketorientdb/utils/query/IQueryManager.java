package ru.ydn.wicket.wicketorientdb.utils.query;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import org.apache.wicket.model.IModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

/**
 * Interface for different implementation of query managers 
 */
public interface IQueryManager {
	public String getProjection();
	public String getSql();
	public String getCountSql();
	public boolean hasOrderBy();
	public String prepareSql(Integer first, Integer count, String sortBy, boolean isAscending);
	public void addFilterCriteriaManager(String field, IFilterCriteriaManager manager);
	public IFilterCriteriaManager getFilterCriteriaManager(String field);
}
