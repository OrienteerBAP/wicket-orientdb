package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.base.Strings;
import org.apache.wicket.model.IModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.IFilterValue;

/**
 * Equals filter
 */
public class EqualsFilterCriteria extends AbstractFilterCriteria {

    private final IFilterValue filterValue;

    public EqualsFilterCriteria(String field, IFilterValue filterValue, IModel<Boolean> join) {
        super(field, FilterCriteriaType.EQUALS.getName() +"_" + field, join);
        this.filterValue = filterValue;
    }

    @Override
    protected String apply(String field) {
        String value = filterValue.getString();
        if (Strings.isNullOrEmpty(value))
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append(field);
        sb.append(" = '");
        sb.append(value);
        sb.append("'");
        return sb.toString();
    }
}
