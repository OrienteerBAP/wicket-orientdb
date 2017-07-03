package ru.ydn.wicket.wicketorientdb.utils.query.filter.number;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilter;

/**
 * value - value for equals
 * SELECT FROM class WHERE num = value
 */
public class EqualsNumberFilter extends AbstractFilter {

    private final Integer value;

    public EqualsNumberFilter(String field, Integer value, boolean join) {
        super(field, join);
        this.value = value;

    }

    @Override
    public String apply(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append(field);
        sb.append(" = ");
        sb.append(value.toString());
        return sb.toString();
    }


}
