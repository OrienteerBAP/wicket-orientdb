package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IClusterable;

import java.util.List;

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
     * Get filtered field name
     * @return filtered field name
     */
    public String getField();

    /**
     * Get join
     * @return {@link IModel<Boolean>} if true - result of filtering will be include to result
     */
    public IModel<Boolean> getJoinModel();
    public String getName();
}
