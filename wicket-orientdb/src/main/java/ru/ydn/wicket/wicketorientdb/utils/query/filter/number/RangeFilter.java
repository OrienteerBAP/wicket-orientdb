package ru.ydn.wicket.wicketorientdb.utils.query.filter.number;

import java.util.List;

/**
 * first - start of range
 * second - end of range
 * SELECT FROM class WHERE num BETWEEN first AND second
 */
class RangeFilter implements INumberFilter {

    private final Integer first;
    private final Integer second;
    private boolean join;

    public RangeFilter(Integer first, Integer second, boolean join) {
        this.first = first;
        this.second = second;
        this.join = join;
    }

    @Override
    public String apply(String field) {
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
    public void setJoin(boolean join) {
        this.join = join;
    }

    @Override
    public String toString() {
        return "RangeFilter{" +
                "first=" + first +
                ", second=" + second +
                ", join=" + join +
                '}';
    }
}
