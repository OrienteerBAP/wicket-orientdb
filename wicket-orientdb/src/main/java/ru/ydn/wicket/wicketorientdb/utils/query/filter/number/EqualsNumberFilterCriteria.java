package ru.ydn.wicket.wicketorientdb.utils.query.filter.number;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilterCriteria;

/**
 * value - value for equals
 * SELECT FROM class WHERE num = value
 */
public class EqualsNumberFilterCriteria extends AbstractFilterCriteria {

    private final Integer value;

    public EqualsNumberFilterCriteria(String field, Integer value, boolean join) {
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
