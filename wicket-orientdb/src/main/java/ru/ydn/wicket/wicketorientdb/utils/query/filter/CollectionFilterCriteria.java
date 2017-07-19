package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

import java.util.Collection;

/**
 * Collection filter
 * SELECT FROM Class WHERE num IN ['1', '2', '3']
 */
public class CollectionFilterCriteria extends AbstractFilterCriteria {

    public <T> CollectionFilterCriteria(String field, IModel<Collection<T>> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected String apply(String field) {
        IModel<Collection<?>> collectionModel = (IModel<Collection<?>>) getModel();
        if (collectionModel.getObject() == null || collectionModel.getObject().isEmpty())
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append(field).append(" IN :").append(getName());
        return sb.toString();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.COLLECTION;
    }
}
