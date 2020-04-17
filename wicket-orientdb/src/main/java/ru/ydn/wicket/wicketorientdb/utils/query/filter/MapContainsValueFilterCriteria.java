package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

/**
 * SELECT FROM Class WHERE map CONTAINSVALUE "value"
 */
public class MapContainsValueFilterCriteria extends AbstractFilterCriteria {
	private static final long serialVersionUID = 1L;

	public MapContainsValueFilterCriteria(String field, IModel<?> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    protected String apply(String field) {
        return field + " CONTAINSVALUE :" + getName();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.CONTAINS_VALUE;
    }
}
