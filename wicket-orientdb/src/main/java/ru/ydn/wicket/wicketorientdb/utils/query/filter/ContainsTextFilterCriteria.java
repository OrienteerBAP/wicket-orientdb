package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.base.Strings;
import org.apache.wicket.model.IModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.IFilterValue;

/**
 * Contains text filter
 * SELECT FROM Class WHERE name CONTAINSTEXT 'text'
 */
public class ContainsTextFilterCriteria extends AbstractFilterCriteria {

    private final IFilterValue value;

    public ContainsTextFilterCriteria(String field, IFilterValue value, IModel<Boolean> join) {
        super(field, FilterCriteriaType.CONTAINS_TEXT + "_" + field, join);
        this.value = value;
    }

    @Override
    protected String apply(String field) {
        String filter = value.getString();
        if (Strings.isNullOrEmpty(filter))
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append(field)
                .append(" CONTAINSTEXT '")
                .append(filter)
                .append("'");
        return sb.toString();
    }
}
