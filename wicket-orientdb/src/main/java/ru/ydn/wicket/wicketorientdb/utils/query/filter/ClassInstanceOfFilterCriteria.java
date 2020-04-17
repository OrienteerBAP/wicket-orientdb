package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

/**
 * Instance of filter
 * select from Parent where @class instanceof Child
 */
public class ClassInstanceOfFilterCriteria extends AbstractFilterCriteria {

	private static final long serialVersionUID = 1L;

	public ClassInstanceOfFilterCriteria(String field, IModel<String> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    protected String apply(String field) {
        // TODO: access to model by name instead by value after fix fot PR: https://github.com/orientechnologies/orientdb/issues/8797
        return field + " instanceof \"" + getModel().getObject() + "\"";
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.CLASS_INSTANCE_OF;
    }
}
