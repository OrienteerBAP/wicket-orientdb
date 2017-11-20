package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

/**
 * Filter criteria for EMBEDDED collections
 * SELECT FROM Test WHERE embedded_collection CONTAINS value
 */
public class EmbeddedCollectionContainsValueFilterCriteria extends AbstractFilterCriteria {

    public <T> EmbeddedCollectionContainsValueFilterCriteria(String field, IModel<T> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    protected String apply(String field) {
        return field + " CONTAINS :" + getName();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.EMBEDDED_COLLECTION_CONTAINS;
    }
}
