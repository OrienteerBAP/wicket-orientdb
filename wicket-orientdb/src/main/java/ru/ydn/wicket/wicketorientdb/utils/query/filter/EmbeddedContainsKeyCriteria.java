package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

/**
 * Embedded contains key filter criteria
 * SELECT FROM Test WHERE embedded.keys() CONTAINS :myKey
 * @param <T> type of model for filtering
 */
public class EmbeddedContainsKeyCriteria<T> extends AbstractFilterCriteria<T> {
	
	private static final long serialVersionUID = 1L;

	public EmbeddedContainsKeyCriteria(String field, IModel<T> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    protected String apply(String field) {
        return " " + field + ".keys() CONTAINS :" + getName();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.EMBEDDED_CONTAINS_KEY;
    }
}
