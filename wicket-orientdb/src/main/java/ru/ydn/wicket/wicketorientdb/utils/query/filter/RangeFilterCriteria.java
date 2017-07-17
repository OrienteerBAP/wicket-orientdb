package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

import java.util.Collection;

/**
 * Range filter
 * SELECT FROM Class WHERE num BETWEEN '1' AND '5'
 */
public class RangeFilterCriteria extends AbstractFilterCriteria {

    public RangeFilterCriteria(String field, IModel<Collection<IModel<?>>> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    protected String apply(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append(field)
                .append(" BETWEEN :")
                .append(getName())
                .append("0 AND :")
                .append(getName())
                .append("1");
        return sb.toString();
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.RANGE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isEmpty() {
        Collection<IModel<?>> collection = (Collection<IModel<?>>) getModel().getObject();
        for (IModel<?> model : collection) {
            if (model == null || model.getObject() == null) {
                return true;
            }
        }
        return false;
    }
}
