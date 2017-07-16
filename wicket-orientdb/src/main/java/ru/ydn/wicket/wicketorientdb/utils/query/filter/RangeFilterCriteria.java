package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.base.Strings;
import org.apache.wicket.model.IModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.IFilterValue;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.CollectionFilterValue;

/**
 * Range filter
 * SELECT FROM Class WHERE num BETWEEN '1' AND '5'
 */
public class RangeFilterCriteria extends AbstractFilterCriteria {

    private final IFilterValue filterValue;

    public RangeFilterCriteria(String field, CollectionFilterValue<?> filterValue, IModel<Boolean> join) {
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
