package ru.ydn.wicket.wicketorientdb.utils.query.filter;

public abstract class AbstractFilterCriteria implements IFilterCriteria {

    private final String fieldName;

    protected AbstractFilterCriteria(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String apply(String sql) {
        StringBuilder sb = new StringBuilder(sql.length() * 2);
        sb.append(sql);
        if (sql.toUpperCase().contains("WHERE")) {
            sb.append(" AND ( ");
            sb.append(apply());
            sb.append(" )");
        } else {
            sb.append(" WHERE ");
            sb.append(apply());
        }
        return sb.toString();
    }

    protected abstract String apply();

    @Override
    public String getFieldName() {
        return fieldName;
    }
}
