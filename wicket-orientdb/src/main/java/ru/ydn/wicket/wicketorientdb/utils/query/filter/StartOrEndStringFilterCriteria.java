package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.base.Strings;
import org.apache.wicket.model.IModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.IFilterValue;

/**
 * Filter for search documents which have string field 'field' and starts or end with 'value'
 * Example:
 * SELECT FROM Class WHERE field LIKE 'value%'    - starts
 * or
 * SELECT FROM Class WHERE field LIKE '%value'    - ends
 */
public class StartOrEndStringFilterCriteria extends AbstractFilterCriteria {

    private final IFilterValue filterValue;
    private final boolean start;

    public StartOrEndStringFilterCriteria(String field, IFilterValue filterValue,  boolean start, IModel<Boolean> join) {
        super(field, start ? FilterCriteriaType.STRING_START.getName() + field :
                FilterCriteriaType.STRING_END.getName() + field, join);
        this.filterValue = filterValue;
        this.start = start;
    }

    @Override
    protected String apply(String field) {
        String filter = filterValue.getString();
        if (Strings.isNullOrEmpty(filter))
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append(field);
        sb.append(" LIKE ");
        sb.append("'");
        if (!start) sb.append("%");
        sb.append(filter);
        if (start) sb.append("%");
        sb.append("'");
        return sb.toString();
    }


}
