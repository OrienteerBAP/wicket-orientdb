package ru.ydn.wicket.wicketorientdb.utils.query.filter.embedded;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilterCriteria;

import java.util.Map;

/**
 * Filter for search documents which embedded fields are equals with input data.
 */
public class EqualsEmbeddedFilterCriteria extends AbstractFilterCriteria {

    private final Map<String, String> fieldAndValue;

    public EqualsEmbeddedFilterCriteria(String field, Map<String, String> fieldAndValue, boolean join) {
        super(field, join);
        this.fieldAndValue = fieldAndValue;
    }

    @Override
    protected String apply(String filteringField) {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (String field : fieldAndValue.keySet()) {
            String value = fieldAndValue.get(field);
            sb.append(filteringField).append(".");
            sb.append(field);
            sb.append(" = '");
            sb.append(value);
            sb.append("'");
            if (counter != fieldAndValue.size() - 1) sb.append(" AND ");
            counter++;
        }
        return sb.toString();
    }
}
