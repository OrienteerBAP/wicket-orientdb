package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

/**
 * Contains text filter
 * SELECT FROM Class WHERE name CONTAINSTEXT 'text'
 */
public class ContainsTextFilterCriteria extends AbstractFilterCriteria {

    public ContainsTextFilterCriteria(String field, IModel<String> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    protected String apply(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append(field)
                .append(" CONTAINSTEXT :")
                .append(getName());
        return sb.toString();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.CONTAINS_TEXT;
    }
}
