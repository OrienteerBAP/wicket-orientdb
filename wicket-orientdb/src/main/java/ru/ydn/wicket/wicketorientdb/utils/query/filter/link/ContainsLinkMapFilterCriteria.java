package ru.ydn.wicket.wicketorientdb.utils.query.filter.link;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilterCriteria;

import java.util.List;

/**
 * Filter for search documents by values which contains in link map
 */
public class ContainsLinkMapFilterCriteria extends AbstractFilterCriteria {

    private final List<String> keys;
    private final List<String> orids;

    private final boolean and;

    public ContainsLinkMapFilterCriteria(String field, List<String> keys, List<String> orids, boolean and, boolean join) {
        super(field, join);
        this.keys = keys;
        this.orids = orids;
        this.and = and;
    }

    @Override
    protected String apply() {
        StringBuilder sb = new StringBuilder();
        if (keys != null && !keys.isEmpty()) {
            appendList(sb, keys, "CONTAINSKEY");
        }

        if (orids != null && !orids.isEmpty()) {
            if (keys != null && !keys.isEmpty()) {
                if (and) sb.append(" AND ");
                else sb.append(" OR ");
            }
            appendList(sb, orids, "CONTAINSVALUE");
        }
        return sb.toString();
    }

    private void appendList(StringBuilder sb, List<String> list, String statement) {
        for (int i = 0; i < list.size(); i++) {
            sb.append(getField());
            sb.append(" ").append(statement).append(" ");
            sb.append("'");
            sb.append(list.get(i));
            sb.append("'");
            if (i != list.size() - 1) {
                if (and) sb.append(" AND ");
                else sb.append(" OR ");
            }
        }
    }
}
