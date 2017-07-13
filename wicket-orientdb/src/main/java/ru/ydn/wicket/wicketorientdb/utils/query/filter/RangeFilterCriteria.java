package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.base.Strings;
import org.apache.wicket.model.IModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.IFilterValue;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.ListFilterValue;

/**
 * Range filter
 */
public class RangeFilterCriteria extends AbstractFilterCriteria {

    private final IFilterValue filterValue;

    public RangeFilterCriteria(String field, ListFilterValue<?> filterValue, IModel<Boolean> join) {
        super(field, FilterCriteriaType.RANGE.getName() + field, join);
        this.filterValue = filterValue;
    }

    @Override
    protected String apply(String field) {
        String filter = filterValue.getString();
        if (Strings.isNullOrEmpty(filter) || !filter.contains(IFilterValue.VALUE_SEPARATOR))
            return null;
        filter = filter.replaceAll(IFilterValue.VALUE_SEPARATOR, " AND ");
        StringBuilder sb = new StringBuilder();
        sb.append(field);
        sb.append(" BETWEEN ");
        sb.append(filter);
        return sb.toString();
    }
}
