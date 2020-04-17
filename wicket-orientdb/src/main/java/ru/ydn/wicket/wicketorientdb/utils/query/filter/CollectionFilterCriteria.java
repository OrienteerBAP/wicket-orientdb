package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

import java.util.Collection;

/**
 * Collection filter
 * SELECT FROM Class WHERE num IN ['1', '2', '3']
 */
public class CollectionFilterCriteria extends AbstractFilterCriteria {

	private static final long serialVersionUID = 1L;

	public <T> CollectionFilterCriteria(String field, IModel<Collection<T>> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected String apply(String field) {
        return field + " IN :" + getName();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.COLLECTION;
    }
}
