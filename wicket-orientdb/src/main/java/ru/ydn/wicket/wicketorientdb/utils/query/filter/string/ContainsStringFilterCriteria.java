package ru.ydn.wicket.wicketorientdb.utils.query.filter.string;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilterCriteria;

/**
 * Class for generation SQL: "if field contains string".
 * SELECT FROM class WHERE stringField LIKE '%myString%'
 */
public class ContainsStringFilterCriteria extends AbstractFilterCriteria {

    private final String value;

    public ContainsStringFilterCriteria(String field, String value, boolean contains) {
        super(field, contains);
        this.value = value;
    }

    @Override
    public String apply(String filter) {
        StringBuilder sb = new StringBuilder();
        sb.append(filter);
        sb.append(" LIKE '")
                .append("%")
                .append(value)
                .append("%'");
        return sb.toString();
    }
}
