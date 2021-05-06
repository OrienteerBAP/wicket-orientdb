package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

/**
 * Filter criteria for EMBEDDED collections
 * SELECT FROM Test WHERE embedded_collection CONTAINS value
 * @param <T> type of model for filtering
 */
public class EmbeddedCollectionContainsValueFilterCriteria<T> extends AbstractFilterCriteria<T> {

	private static final long serialVersionUID = 1L;

	public EmbeddedCollectionContainsValueFilterCriteria(String field, IModel<T> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    protected String apply(String field) {
        return field + " CONTAINS :" + getPSVariableName();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.EMBEDDED_COLLECTION_CONTAINS;
    }
}
