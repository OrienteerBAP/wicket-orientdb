package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

/**
 * Embedded contains value filter criteria
 * SELECT FROM Test WHERE embedded.values() CONTAINS :myValue
 */
public class EmbeddedContainsValueCriteria extends AbstractFilterCriteria {

    public EmbeddedContainsValueCriteria(String field, IModel<?> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    protected String apply(String field) {
        return " " + field + ".values() CONTAINS :" + getName();
    }


    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.EMBEDDED_CONTAINS_VALUE;
    }
}
