package ru.ydn.wicket.wicketorientdb.utils.query.filter;

/**
 * Interface for save filter criteria and generate SQL depending on filter criteria
 */
public interface IFilterCriteria {
    public String apply(String sql);
    public IFilterCriteria setFilter(IFilter filter);
}
