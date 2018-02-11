package ru.ydn.wicket.wicketorientdb.utils.query;

import org.apache.wicket.util.io.IClusterable;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteriaManager;

import java.util.Collection;

/**
 * Interface for different implementation of query managers 
 */
public interface IQueryManager extends IClusterable {
	public String getProjection();

	/**
	 * @return sql generated from {@link IQueryManager} with included filters statements
	 */
	public String getSql();

	/**
	 * @return sql for count how much {@link com.orientechnologies.orient.core.record.impl.ODocument}
	 * can select with included filters statements
	 */
	public String getCountSql();

	/**
	 * @return true if is some order by
	 */
	public boolean hasOrderBy();

	/**
	 * Prepare sql
	 * @param first start
	 * @param count end
	 * @param sortBy sort field
	 * @param isAscending if true order by ascending, order by descending otherwise
	 * @return prepared sql
	 */
	public String prepareSql(Integer first, Integer count, String sortBy, boolean isAscending);

	/**
	 * Add new {@link IFilterCriteriaManager} for current {@link IQueryManager}
	 * @param field filtered field name
	 * @param manager {@link IFilterCriteriaManager} for add
	 */
	public void addFilterCriteriaManager(String field, IFilterCriteriaManager manager);

	/**
	 * @return unmodifiable {@link Collection} with all {@link IFilterCriteriaManager}
	 * for current {@link IQueryManager}
	 */
	public Collection<IFilterCriteriaManager> getFilterCriteriaManagers();

	/**
	 * Get {@link IFilterCriteriaManager} by filtered field name
	 * @param field filtered field name
	 * @return {@link IFilterCriteriaManager} for filtered field
	 */
	public IFilterCriteriaManager getFilterCriteriaManager(String field);

	/**
	 * Delete all {@link IFilterCriteriaManager} for current {@link IQueryManager}
	 */
	public void clearFilterCriteriaManagers();

}
