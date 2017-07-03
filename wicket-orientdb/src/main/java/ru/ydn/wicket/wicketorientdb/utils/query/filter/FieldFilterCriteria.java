package ru.ydn.wicket.wicketorientdb.utils.query.filter;

/**
 * Abstract class for IFilterCriteria
 */
public class FieldFilterCriteria implements IFilterCriteria {

    private IFilter filter;

    public FieldFilterCriteria(IFilter filter) {
        this.filter = filter;
    }

    public FieldFilterCriteria() {}

    @Override
    public String apply(String sql) {
        if (filter == null)
            return sql;
        StringBuilder sb = new StringBuilder(sql.length() * 2);
        sb.append(sql);
        if (sql.toUpperCase().contains("WHERE")) {
            sb.append(" AND ( ");
            sb.append(filter.apply());
            sb.append(" )");
        } else {
            sb.append(" WHERE ");
            sb.append(filter.apply());
        }
        return sb.toString();
    }

    @Override
    public IFilterCriteria setFilter(IFilter filter) {
        this.filter = filter;
        return this;
    }

}
