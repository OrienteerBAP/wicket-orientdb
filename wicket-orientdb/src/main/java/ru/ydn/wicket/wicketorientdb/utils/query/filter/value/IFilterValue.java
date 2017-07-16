package ru.ydn.wicket.wicketorientdb.utils.query.filter.value;

import org.apache.wicket.util.io.IClusterable;

import java.util.List;

/**
 * Interface which represents filter value for {@link ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteria}
 */
public interface IFilterValue extends IClusterable {

    /**
     * @return {@link List<String>} unmodifiable list which is representation of value(s)
     */
    public List<String> toStringList();

}
