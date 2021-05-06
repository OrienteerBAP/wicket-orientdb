package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

import java.util.Collection;

/**
 * Collection filter
 * SELECT FROM Class WHERE num IN ['1', '2', '3']
 * @param <C> type of collection objects for filtering
 */
public class CollectionFilterCriteria<C> extends AbstractFilterCriteria<Collection<C>> {

	private static final long serialVersionUID = 1L;

	public CollectionFilterCriteria(String field, IModel<Collection<C>> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected String apply(String field) {
        return field + " IN :" + getPSVariableName();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.COLLECTION;
    }
}
