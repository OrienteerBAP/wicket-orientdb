package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Range filter
 * SELECT FROM Class WHERE num BETWEEN '1' AND '5'
 * @param <C> type of objects for between range for filtering
 */
public class RangeFilterCriteria<C> extends AbstractFilterCriteria<List<C>> {

	private static final long serialVersionUID = 1L;

	public <T> RangeFilterCriteria(String field, IModel<List<C>> model, IModel<Boolean> join) {
        super(field, model, join);
    }

    @Override
    protected String apply(String field) {
        String result;
        List<?> list = (List<?>) getModel().getObject();
        if (list.get(0) != null && list.get(1) != null) {
            result = field + " BETWEEN :" + getName() + "0 AND :" + getName() + "1";
        } else if (list.get(0) != null && list.get(1) == null) {
            result = field + " >= :" + getName();
        } else {
            result = field + " <= :" + getName();
        }
        return result;
    }

    @Override
    public FilterCriteriaType getFilterCriteriaType() {
        return FilterCriteriaType.RANGE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isEmpty() {
        List<?> list = (List<?>) getModel().getObject();
        return list.isEmpty() || list.size() != 2 || list.get(0) == null && list.get(1) == null;
    }
}
