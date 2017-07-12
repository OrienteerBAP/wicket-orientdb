package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.base.Strings;
import org.apache.wicket.model.IModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.IFilterValue;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.ListFilterValue;

/**
 * List filter
 */
public class ListFilterCriteria extends AbstractFilterCriteria {

    private final IFilterValue filterValue;

    public ListFilterCriteria(String field, ListFilterValue<?> filterValue, IModel<Boolean> join) {
        super(field, FilterCriteriaType.LIST + "_" + field, join);
        this.filterValue = filterValue;
    }


    @Override
    protected String apply(String field) {
        String value = filterValue.getString();
        if (Strings.isNullOrEmpty(value))
            return null;
        value = value.replaceAll(IFilterValue.VALUE_SEPARATOR, ",");
        StringBuilder sb = new StringBuilder();

        sb.append(field);
        sb.append(" IN [");
        sb.append(value);
        sb.append("]");
        return sb.toString();
    }
}
