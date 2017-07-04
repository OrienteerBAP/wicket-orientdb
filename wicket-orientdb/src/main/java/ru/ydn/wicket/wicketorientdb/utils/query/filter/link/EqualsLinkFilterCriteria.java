package ru.ydn.wicket.wicketorientdb.utils.query.filter.link;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilterCriteria;

import java.util.Map;

/**
 * Filter for equals link fields with input data or RID in document.
 */
public class EqualsLinkFilterCriteria extends AbstractFilterCriteria {

    private final Map<String, String> fieldAndValue;
    private final String orid;


    public EqualsLinkFilterCriteria(String field, Map<String, String> fieldAndValue, boolean join) {
        super(field, join);
        this.fieldAndValue = fieldAndValue;
        this.orid = null;
    }

    public EqualsLinkFilterCriteria(String field, String orid, boolean join) {
        super(field, join);
        this.fieldAndValue = null;
        this.orid = orid;
    }

    @Override
    protected String apply() {
        return fieldAndValue != null ? filterByValues() : filterByOrid();
    }

    private String filterByOrid() {
        StringBuilder sb = new StringBuilder();
        sb.append(getField());
        sb.append(" = ");
        if (!orid.startsWith("#")) sb.append("#");
        sb.append(orid);
        return sb.toString();
    }

    private String filterByValues() {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (String field : fieldAndValue.keySet()) {
            String value = fieldAndValue.get(field);
            sb.append(getField()).append(".");
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
