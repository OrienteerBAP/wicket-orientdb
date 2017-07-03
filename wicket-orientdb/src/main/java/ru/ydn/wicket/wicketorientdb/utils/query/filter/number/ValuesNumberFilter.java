package ru.ydn.wicket.wicketorientdb.utils.query.filter.number;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilter;

import java.util.List;

/**
 * array of values
 * SELECT FROM class WHERE num IN (value1, value2, ..., valueN)
 */
public class ValuesNumberFilter extends AbstractFilter {

    private final List<Integer> values;

    public ValuesNumberFilter(String field, List<Integer> values, boolean join) {
        super(field, join);
        this.values = values;
    }

    @Override
    public String apply(String field) {
        StringBuilder sb = new StringBuilder();

        sb.append(field);
        sb.append(" IN [");
        appendValues(sb);
        sb.append("]");
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
    public String toString() {
        return super.toString();
    }
}
