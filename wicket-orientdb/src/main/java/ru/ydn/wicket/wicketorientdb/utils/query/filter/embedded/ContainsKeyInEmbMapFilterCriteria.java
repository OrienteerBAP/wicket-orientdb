package ru.ydn.wicket.wicketorientdb.utils.query.filter.embedded;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilterCriteria;

import java.util.List;

/**
 * Search documents which contains map and that maps contains input keys.
 */
public class ContainsKeyInEmbMapFilterCriteria extends AbstractFilterCriteria {

    private final List<String> keys;
    private final boolean and;

    public ContainsKeyInEmbMapFilterCriteria(String field, List<String> keys, boolean and, boolean join) {
        super(field, join);
        this.keys = keys;
        this.and = and;
    }

    @Override
    protected String apply(String field) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < keys.size(); i++) {
            sb.append(field);
            sb.append(" CONTAINSKEY '");
            sb.append(keys.get(i));
            sb.append("'");
            if (i != keys.size() - 1) sb.append(and ? " AND " : " OR ");
        }
        return sb.toString();
    }
}
