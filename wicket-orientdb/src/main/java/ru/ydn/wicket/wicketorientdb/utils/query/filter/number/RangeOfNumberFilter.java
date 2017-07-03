package ru.ydn.wicket.wicketorientdb.utils.query.filter.number;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilter;

/**
 * first - start of range
 * second - end of range
 * SELECT FROM class WHERE num BETWEEN first AND second
 */
public class RangeOfNumberFilter extends AbstractFilter {

    private final Integer first;
    private final Integer second;


    public RangeOfNumberFilter(String field, Integer first, Integer second, boolean join) {
        super(field, join);
        this.first = first;
        this.second = second;
    }

    @Override
    public String apply(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append(field);
        sb.append(" BETWEEN ");
        sb.append(first.toString());
        sb.append(" AND ");
        sb.append(second.toString());
        return sb.toString();
    }

}
