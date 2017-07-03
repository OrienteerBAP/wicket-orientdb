package ru.ydn.wicket.wicketorientdb.utils.query.filter;

/**
 * Class that contains general actions for filters.
 */
public abstract class AbstractFilter implements IFilter {

    private final String field;
    private final boolean join;

    public AbstractFilter(String field, boolean join) {
        this.field = field;
        this.join = join;
    }

    @Override
    public String apply() {
        StringBuilder sb = new StringBuilder();
        if (!join) sb.append(" NOT(");
        sb.append(apply(field));
        if (!join) sb.append(")");
        return sb.toString();
    }

    protected abstract String apply(String field);
}
