package ru.ydn.wicket.wicketorientdb.utils.query.filter.embedded;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteria;

import java.util.List;
import java.util.Map;

/**
 * Filter for search documents with equals values in embedded list
 */
public class EqualsEmbCollectionFilterCriteria extends AbstractFilterCriteria {

    private final List<Map<Integer, IFilterCriteria>> filter;
    private final boolean and;

    public EqualsEmbCollectionFilterCriteria(String field, List<Map<Integer, IFilterCriteria>> filter,
                                             boolean and, boolean join) {
        super(field, join);
        this.filter = filter;
        this.and = and;
    }

    @Override
    protected String apply(String field) {
        StringBuilder sb = new StringBuilder();
        String template = "%s[%d].%s";
        int i = 0;
        for (Map<Integer, IFilterCriteria> mapFilter : filter) {
            for (Integer counter : mapFilter.keySet()) {
                IFilterCriteria filterCriteria = mapFilter.get(counter);
                String filterCriteriaField = filterCriteria.getField();
                filterCriteria.setField(String.format(template, field, counter, filterCriteriaField));
                sb.append(" ");
                sb.append(filterCriteria.apply());
                filterCriteria.setField(filterCriteriaField);
                if (i != filter.size() - 1) {
                    if (and) {
                        sb.append(" AND ");
                    } else {
                        sb.append(" OR ");
                    }
                }
                i++;
            }
        }
        return sb.toString();
    }
}
