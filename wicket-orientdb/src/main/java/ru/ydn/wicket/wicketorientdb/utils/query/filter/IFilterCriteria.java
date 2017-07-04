package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.util.io.IClusterable;

/**
 * Interface for save filter criteria and generate SQL depending on filter criteria
 */
public interface IFilterCriteria extends IClusterable {
    public String apply();
    public String getField();
    public void setField(String field);
}
