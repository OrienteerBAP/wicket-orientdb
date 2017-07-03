package ru.ydn.wicket.wicketorientdb.utils.query.filter.number;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilter;

/**
 * value - value for equals
 * SELECT FROM class WHERE num = value
 */
public class EqualsNumberFilter implements IFilter {

    private final String field;
    private final Integer value;
    private boolean join;

    public EqualsNumberFilter(String field, Integer value, boolean join) {
        this.field = field;
        this.value = value;
        this.join = join;
    }

    @Override
    public String apply() {
        StringBuilder sb = new StringBuilder();
        sb.append(field);
        if (join) {
            sb.append(" = ");
            sb.append(value.toString());
        } else {
            sb.append(" <> ");
            sb.append(value.toString());
        }
        return sb.toString();
    }


}
