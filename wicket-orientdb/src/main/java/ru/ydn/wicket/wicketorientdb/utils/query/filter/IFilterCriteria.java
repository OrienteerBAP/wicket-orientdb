package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;

/**
 * Interface for save filter criteria and generate SQL depending on filter criteria
 */
public interface IFilterCriteria extends IClusterable {
    /**
     * Apply filter
     * @return sql of filter
     */
    public String apply();

    /**
     * Get join
     * @return {@link IModel} with boolean. If true - result of filtering will be include to result
     */
    public IModel<Boolean> getJoinModel();

    /**
     * @return filtered field name
     */
    public String getField();

    /**
     * @return filter name
     */
    public String getName();

    /**
     * @return model for filtering
     */
    public IModel<?> getModel();

    /**
     * @return {@link FilterCriteriaType} for current FilterCriteria
     */
    public FilterCriteriaType getFilterCriteriaType();

    /**
     * Check if current {@link IFilterCriteria} is empty
     * @return true if current {@link IFilterCriteria} is empty
     */
    public boolean isEmpty();
}
