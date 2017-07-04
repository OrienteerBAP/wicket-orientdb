package ru.ydn.wicket.wicketorientdb.utils.query.filter;

/**
 * Abstract class for IFilterCriteria
 */
public abstract class AbstractFilterCriteria implements IFilterCriteria {

    private final String field;
    private final boolean join;

    public AbstractFilterCriteria(String field, boolean join) {
        this.field = field;
        this.join = join;
    }

    @Override
    public String apply(String sql) {
        if (getField() == null)
            return sql;
        boolean containsWhere = sql.toUpperCase().contains("WHERE");
        StringBuilder sb = new StringBuilder(sql.length() * 2);
        sb.append(sql);
        if (containsWhere) sb.append(" AND(");
        else sb.append(" WHERE ");
        if (!join) sb.append("NOT(");
        sb.append(apply());
        if (!join) sb.append(")");
        if (containsWhere) sb.append(" )");
        return sb.toString();
    }

    protected abstract String apply();

    @Override
    public String getField() {
        return field;
    }
}
