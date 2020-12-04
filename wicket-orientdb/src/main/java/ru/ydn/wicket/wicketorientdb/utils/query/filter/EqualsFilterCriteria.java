package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

/**
 * Equals filter
 * SELECT FROM Class WHERE num = '1'
 * @param <T> type of model for filtering
 */
public class EqualsFilterCriteria<T> extends AbstractFilterCriteria<T> {

	private static final long serialVersionUID = 1L;

	public EqualsFilterCriteria(String field, IModel<T> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    protected String apply(String field) {
        return field + " = :" + getName();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.EQUALS;
    }
}
