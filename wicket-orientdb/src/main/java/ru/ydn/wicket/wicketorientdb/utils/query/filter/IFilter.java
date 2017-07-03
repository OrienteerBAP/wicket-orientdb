package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.util.io.IClusterable;

/**
 * Interface for generating SQL for number fields
 */
public interface IFilter extends IClusterable {
    public String apply();
}
