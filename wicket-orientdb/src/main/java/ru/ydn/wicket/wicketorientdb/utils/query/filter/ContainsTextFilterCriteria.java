package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

/**
 * Contains text filter
 * SELECT FROM Class WHERE name CONTAINSTEXT 'text'
 */
public class ContainsTextFilterCriteria extends AbstractFilterCriteria<String> {

	private static final long serialVersionUID = 1L;
	
	private boolean toStringRequired;

	public ContainsTextFilterCriteria(String field, IModel<String> model, boolean toStringRequired, IModel<Boolean> join) {
        super(field, model, join);
        this.toStringRequired = toStringRequired;
    }

    @Override
    protected String apply(String field) {
        return (toStringRequired?field + ".asString()" : field) + " CONTAINSTEXT :" + getName();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.CONTAINS_TEXT;
    }
}
