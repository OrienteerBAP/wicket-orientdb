package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

/**
 * Contains text filter
 * SELECT FROM Class WHERE name CONTAINSTEXT 'text'
 */
public class ContainsTextFilterCriteria extends AbstractFilterCriteria {

	private static final long serialVersionUID = 1L;

	public ContainsTextFilterCriteria(String field, IModel<String> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    protected String apply(String field) {
        return field + " CONTAINSTEXT :" + getName();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.CONTAINS_TEXT;
    }
}
