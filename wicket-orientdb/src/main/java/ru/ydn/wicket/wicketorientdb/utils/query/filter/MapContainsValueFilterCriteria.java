package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

/**
 * SELECT FROM Class WHERE map CONTAINSVALUE "value"
 * @param <T> type of model for filtering
 */
public class MapContainsValueFilterCriteria<T> extends AbstractFilterCriteria<T> {
	private static final long serialVersionUID = 1L;

	public MapContainsValueFilterCriteria(String field, IModel<T> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    protected String apply(String field) {
        return field + " CONTAINSVALUE :" + getPSVariableName();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.CONTAINS_VALUE;
    }
}
