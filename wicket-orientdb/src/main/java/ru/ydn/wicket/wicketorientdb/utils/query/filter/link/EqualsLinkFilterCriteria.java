package ru.ydn.wicket.wicketorientdb.utils.query.filter.link;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilterCriteria;

import java.util.Map;

/**
 * Filter for equals link fields with input data in document.
 */
public class EqualsLinkFilterCriteria extends AbstractFilterCriteria {

    private final Map<String, String> fieldAndValue;

    public EqualsLinkFilterCriteria(String linkField, Map<String, String> fieldAndValue, boolean join) {
        super(linkField, join);
        this.fieldAndValue = fieldAndValue;
    }

    @Override
    protected String apply() {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (String field : fieldAndValue.keySet()) {
            String value = fieldAndValue.get(field);
            sb.append(getField()).append(".");
            sb.append(field);
            sb.append(" = '");
            sb.append(value).append("'");
            if (counter != fieldAndValue.size() - 1) sb.append(" AND ");
            counter++;
        }

        return sb.toString();
    }
}
