package ru.ydn.wicket.wicketorientdb.utils.query.filter.number;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilter;

/**
 * first - start of range
 * second - end of range
 * SELECT FROM class WHERE num BETWEEN first AND second
 */
public class RangeNumberFilter implements IFilter {

    private final String field;
    private final Integer first;
    private final Integer second;
    private boolean join;

    public RangeNumberFilter(String field, Integer first, Integer second, boolean join) {
        this.field = field;
        this.first = first;
        this.second = second;
        this.join = join;
    }

    @Override
    public String apply() {
        StringBuilder sb = new StringBuilder();
        if (!join) {
            sb.append(" NOT(");
        }
        sb.append(field);
        sb.append(" BETWEEN ");
        sb.append(first.toString());
        sb.append(" AND ");
        sb.append(second.toString());

        if (!join) sb.append(")");
        return sb.toString();
    }


    @Override
    public String toString() {
        return "RangeNumberFilter{" +
                "first=" + first +
                ", second=" + second +
                ", join=" + join +
                '}';
    }
}
