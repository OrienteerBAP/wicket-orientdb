package ru.ydn.wicket.wicketorientdb.utils.query.filter.link;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilter;

import java.util.Map;

/**
 * Filter for equals link fields with input data in document.
 */
public class EqualsLinkFilter extends AbstractFilter {

    private final Map<String, String> fieldAndValue;

    public EqualsLinkFilter(String linkField, Map<String, String> fieldAndValue, boolean join) {
        super(linkField, join);
        this.fieldAndValue = fieldAndValue;
    }

    @Override
    protected String apply(String linkField) {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (String field : fieldAndValue.keySet()) {
            String value = fieldAndValue.get(field);
            sb.append(linkField).append(".");
            sb.append(field);
            sb.append(" = '");
            sb.append(value).append("'");
            if (counter != fieldAndValue.size() - 1) sb.append(" AND ");
            counter++;
        }

        return sb.toString();
    }
}
