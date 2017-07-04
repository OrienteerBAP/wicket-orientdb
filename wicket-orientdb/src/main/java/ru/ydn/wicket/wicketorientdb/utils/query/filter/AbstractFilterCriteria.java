package ru.ydn.wicket.wicketorientdb.utils.query.filter;

/**
 * Abstract class for IFilterCriteria
 */
public abstract class AbstractFilterCriteria implements IFilterCriteria {

    private String field;
    private final boolean join;

    public AbstractFilterCriteria(String field, boolean join) {
        this.field = field;
        this.join = join;
    }

    @Override
    public String apply() {
        StringBuilder sb = new StringBuilder();
        if (!join) sb.append("NOT(");
        sb.append(apply(getField()));
        if (!join) sb.append(")");
        return sb.toString();
    }


    protected abstract String apply(String field);

    @Override
    public String getField() {
        return field;
    }

    @Override
    public void setField(String field) {
        this.field = field;
    }
}
