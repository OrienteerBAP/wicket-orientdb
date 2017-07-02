package ru.ydn.wicket.wicketorientdb.utils.query.filter;

/**
 * Default filter criteria. No filter.
 * Returns default sql.
 */
public class DefaultFilterCriteria implements IFilterCriteria {

    @Override
    public String apply(String sql) {
        return sql;
    }

    @Override
    public String getFieldName() {
        return null;
    }
}
