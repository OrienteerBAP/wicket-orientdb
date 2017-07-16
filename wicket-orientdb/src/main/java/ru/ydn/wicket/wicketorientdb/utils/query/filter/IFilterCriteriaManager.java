package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Interface for management field filters
 */
public interface IFilterCriteriaManager extends IClusterable {
    /**
     * Apply chain of filters for current field
     * @return SQL which is after WHERE statement
     * Example: a = 1 AND b = 3
     */
    public String apply();

    /**
     * Set which statement will be use between filters (AND or OR)
     * @param and if and true (default and is true) filters are tied with AND statement.
     *           If and is false filters are tied with OR statement.
     */
    public void setAnd(boolean and);

    /**
     * Create equals filter
     * @param model {@link IModel} model of value for filter
     * @param join {@link IModel<Boolean>} if true - result of filtering includes to result of query.
     * @param <T> type of value
     * @return {@link IFilterCriteria} which represents equals filter
     */
    public <T> IFilterCriteria createEqualsFilterCriteria(IModel<T> model, IModel<Boolean> join);

    /**
     * Create collection filter
     * @param models {@link Collection<IModel<T>>} collection of models for filter
     * @param join {@link IModel<Boolean>} if true - result of filtering includes to result of query.
     * @param <T> type of value
     * @return {@link IFilterCriteria} which represents list filter
     */
    public <T> IFilterCriteria createCollectionFilterCriteria(Collection<IModel<T>> models, IModel<Boolean> join);

    /**
     * Create range filter
     * @param models {@link Collection<IModel<T>>} list of 2 models for filter
     * @param join {@link IModel<Boolean>} if true - result of filtering includes to result of query.
     * @param <T> type of value
     * @return {@link IFilterCriteria} which represents range filter
     */
    public <T> IFilterCriteria createRangeFilterCriteria(Collection<IModel<T>> models, IModel<Boolean> join);

    /**
     * Create filter for search string which start or end with model value
     * @param model {@link IModel<String>} value
     * @param start if true search strings which starts with value,
     *              if false search strings which ends with value.
     * @param join {@link IModel<Boolean>} if true - result of filtering includes to result of query.
     * @return {@link IFilterCriteria} which represents start or end string filter
     */
    public IFilterCriteria createStartOrEndStringFilterCriteria(IModel<String> model, boolean start, IModel<Boolean> join);

    /**
     * Create filter for search string which contains model value
     * @param model {@link IModel<String>} value
     * @param join {@link IModel<Boolean>} if true - result of filtering includes to result of query.
     * @return {@link IFilterCriteria} which represents contains string filter
     */
    public IFilterCriteria createContainsStringFilterCriteria(IModel<String> model, IModel<Boolean> join);

    /**
     * Create filter for search {@link ODocument} in collection of links
     * @param model {@link IModel<Collection<ODocument>>} value
     * @param join {@link IModel<Boolean>} if true - result of filtering includes to result of query.
     * @return {@link IFilterCriteria} which represents link collection filter
     */
    public IFilterCriteria createLinkCollectionFilterCriteria(IModel<Collection<ODocument>> model, IModel<Boolean> join);

    /**
     * Set filter for current field
     * @param type {@link FilterCriteriaType} type of filter
     * @param filterCriteria {@link IFilterCriteria} filter
     */
    public void setFilterCriteria(FilterCriteriaType type, IFilterCriteria filterCriteria);

    /**
     * Get filter by type
     * @param type {@link FilterCriteriaType} type of filter
     * @return {@link IFilterCriteria} filter
     */
    public IFilterCriteria getFilterCriteria(FilterCriteriaType type);

    /**
     * @return {@link Map<FilterCriteriaType, IFilterCriteria>}
     */
    public Map<FilterCriteriaType, IFilterCriteria> getFilterCriterias();

    /**
     * Clear filters for current field
     */
    public void clearFilterCriterias();

    /**
     * Check if filter is apply
     * @return true if filter is apply
     */
    public boolean isFilterApply();
}
