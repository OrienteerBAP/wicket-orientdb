package ru.ydn.wicket.wicketorientdb.utils.query.filter.number;

import java.util.List;

/**
 * array of values
 * SELECT FROM class WHERE num IN (value1, value2, ..., valueN)
 */
class ValuesFilter implements INumberFilter {

    private final List<Integer> values;
    private boolean join;

    public ValuesFilter(List<Integer> values, boolean join) {
        this.values = values;
        this.join = join;
    }

    @Override
    public String apply(String field) {
        StringBuilder sb = new StringBuilder();
        if (!join) {
            sb.append(" NOT(");
        }
        sb.append(field);
        sb.append(" IN [");
        appendValues(sb);
        sb.append("]");
        if (!join) sb.append(")");
        return sb.toString();
    }

    private void appendValues(StringBuilder sb) {
        for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i).toString());
            if (i != values.size() - 1) {
                sb.append(", ");
            }
        }
    }

    @Override
    public void setJoin(boolean join) {
        this.join = join;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
