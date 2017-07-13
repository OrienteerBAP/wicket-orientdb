package ru.ydn.wicket.wicketorientdb.utils.query.filter.value;

import org.apache.wicket.util.io.IClusterable;

/**
 * Interface which represents filter value for {@link ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteria}
 */
public interface IFilterValue extends IClusterable {

    /**
     * String separator for complex types like {@link java.util.List}
     */
    public static final String VALUE_SEPARATOR = ",";

    /**
     * Get {@link String} representation f value
     * @return {@link String} representation of value
     */
    public String getString();

}
