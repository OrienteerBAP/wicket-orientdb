package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

import java.util.Collection;

/**
 * Range filter
 * SELECT FROM Class WHERE num BETWEEN '1' AND '5'
 */
public class RangeFilterCriteria extends AbstractFilterCriteria {

    public <T> RangeFilterCriteria(String field, IModel<Collection<T>> model, IModel<Boolean> join) {
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
        Collection<?> collection = (Collection<?>) getModel().getObject();
        if (collection != null) {
            for (Object object : collection) {
                if (object == null) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}
