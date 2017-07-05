package ru.ydn.wicket.wicketorientdb.utils.query.filter.embedded;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilterCriteria;

import java.util.Map;

/**
 * Search documents which contains maps and that map's values are equals to input values
 */
public class EqualsValueInEmbMapFilterCriteria extends AbstractFilterCriteria {

    private final String key;
    private final Map<String, String> fieldAndValue;
    private final boolean and;

    public EqualsValueInEmbMapFilterCriteria(String field, String key, Map<String, String> fieldAndValue, boolean and,
                                             boolean join) {
        super(field, join);
        this.key = key;
        this.fieldAndValue = fieldAndValue;
        this.and = and;
    }

    @Override
    protected String apply(String filteringField) {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        String template = "%s['%s'].%s = '";
        for (String field : fieldAndValue.keySet()) {
            sb.append(String.format(template, filteringField, key, field));
            sb.append(fieldAndValue.get(field));
            sb.append("'");
            if (counter != fieldAndValue.size() - 1) sb.append(and ? " AND " : " OR ");
            counter++;
        }
        return sb.toString();
    }
}
