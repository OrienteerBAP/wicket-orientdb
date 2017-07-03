package ru.ydn.wicket.wicketorientdb.utils.query.filter.string;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilter;

/**
 * Class for generation SQL: "if field contains string".
 * SELECT FROM class WHERE stringField LIKE '%myString%'
 */
public class ContainsStringFilter extends AbstractFilter {

    private final String value;

    public ContainsStringFilter(String field, String value, boolean contains) {
        super(field, contains);
        this.value = value;
    }

    @Override
    public String apply(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append(field);
        sb.append(" LIKE '")
                .append("%")
                .append(value)
                .append("%'");
        return sb.toString();
    }
}
