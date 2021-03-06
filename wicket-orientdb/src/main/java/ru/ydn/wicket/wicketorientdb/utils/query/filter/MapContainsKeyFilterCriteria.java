package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

/**
 * SELECT FROM Class WHERE map CONTAINSKEY 'key'
 * @param <T> type of model for filtering
 */
public class MapContainsKeyFilterCriteria<T> extends AbstractFilterCriteria<T> {
	private static final long serialVersionUID = 1L;

	public MapContainsKeyFilterCriteria(String field, IModel<T> model, IModel<Boolean> join) {
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
