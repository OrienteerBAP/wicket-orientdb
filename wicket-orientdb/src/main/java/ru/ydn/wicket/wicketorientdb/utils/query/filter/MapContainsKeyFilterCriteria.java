package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

/**
 * SELECT FROM Class WHERE map CONTAINSKEY 'key'
 */
public class MapContainsKeyFilterCriteria extends AbstractFilterCriteria {
	private static final long serialVersionUID = 1L;

	public MapContainsKeyFilterCriteria(String field, IModel<?> model, IModel<Boolean> join) {
        super(field, model, join);

    }

    @Override
    protected String apply(String field) {
        return field + " CONTAINSKEY :" + getName();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.CONTAINS_KEY;
    }
}
