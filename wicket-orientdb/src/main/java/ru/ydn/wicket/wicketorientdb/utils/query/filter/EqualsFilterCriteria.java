package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

/**
 * Equals filter
 * SELECT FROM Class WHERE num = '1'
 */
public class EqualsFilterCriteria extends AbstractFilterCriteria {

    public EqualsFilterCriteria(String field, IModel<?> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    protected String apply(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append(field)
                .append(" = :")
                .append(getName());

        return sb.toString();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.EQUALS;
    }
}
