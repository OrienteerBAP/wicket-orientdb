package ru.ydn.wicket.wicketorientdb.utils.query.filter.link;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilterCriteria;

import java.util.List;
import java.util.Map;

/**
 * Filter for equals range of link fields with input data or RID in document.
 */
public class ContainsLinkFilterCriteria extends AbstractFilterCriteria {

    private final Map<String, List<String>> fieldsAndValues;
    private final List<String> orids;

    public ContainsLinkFilterCriteria(String field, Map<String, List<String>> fieldsAndValues, boolean join) {
        super(field, join);
        this.fieldsAndValues = fieldsAndValues;
        this.orids = null;
    }

    public ContainsLinkFilterCriteria(String field, List<String> orids, boolean join) {
        super(field, join);
        this.orids = orids;
        this.fieldsAndValues = null;
    }

    @Override
    protected String apply(String field) {
       return fieldsAndValues != null ? filterByValues(field) : filterByOrid(field);
    }

    private String filterByOrid(String filteringField) {
        StringBuilder sb = new StringBuilder();
        sb.append(filteringField);
        sb.append(" IN [");
        for (int i = 0; i < orids.size(); i++) {
            String orid = orids.get(i);
            if (!orid.startsWith("#")) sb.append("#");
            sb.append(orid);
            if (i != orids.size() - 1) sb.append(", ");
        }
        sb.append(" ]");
        return sb.toString();
    }

    private String filterByValues(String filteringField) {
        StringBuilder sb = new StringBuilder();

        int counter = 0;
        for (String field : fieldsAndValues.keySet()) {
            sb.append(filteringField);
            sb.append(".");
            sb.append(field);
            sb.append(" IN [");
            List<String> values = fieldsAndValues.get(field);
            for (int i = 0; i < values.size(); i++) {
                sb.append("'");
                sb.append(values.get(i));
                sb.append("'");
                if (i != values.size() - 1) sb.append(", ");
            }
            sb.append("]");
            if (counter != fieldsAndValues.size() - 1) sb.append(" AND ");
            counter++;
        }
        return sb.toString();
    }
}
