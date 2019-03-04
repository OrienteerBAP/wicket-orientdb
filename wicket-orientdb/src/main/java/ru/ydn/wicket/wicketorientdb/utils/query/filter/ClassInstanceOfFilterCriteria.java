package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

/**
 * Instance of filter
 * select from Parent where @class instanceof Child
 */
public class ClassInstanceOfFilterCriteria extends AbstractFilterCriteria {

    public ClassInstanceOfFilterCriteria(String field, IModel<?> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    protected String apply(String field) {
        return field + " instanceof :" + getName();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.CLASS_INSTANCE_OF;
    }
}
