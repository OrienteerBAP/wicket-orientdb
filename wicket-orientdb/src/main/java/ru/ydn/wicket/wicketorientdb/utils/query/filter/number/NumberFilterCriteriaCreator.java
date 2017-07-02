package ru.ydn.wicket.wicketorientdb.utils.query.filter.number;

import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteria;

import java.util.List;

public class NumberFilterCriteriaCreator {

    private NumberFilterCriteria filterCriteria;

    public IFilterCriteria createEqualsFilterCriteria(String field, Integer value, boolean join) {
        return getFilterCriteria(field).setNumberFilter(new EqualsFilter(value, join));
    }

    public IFilterCriteria createRangeFilterCriteria(String field, Integer first, Integer second, boolean join) {
        return getFilterCriteria(field).setNumberFilter(new RangeFilter(first, second, join));
    }

    public IFilterCriteria createValuesFilterCriteria(String field, List<Integer> values, boolean join) {
        return getFilterCriteria(field).setNumberFilter(new ValuesFilter(values, join));
    }

    private NumberFilterCriteria getFilterCriteria(String field) {
        return filterCriteria != null && filterCriteria.getFieldName().equals(field)
                ? filterCriteria : new NumberFilterCriteria(field);
    }

}
