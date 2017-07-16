package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import org.apache.wicket.model.IModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.IFilterValue;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.PrimeFilterValue;

import java.util.List;

/**
 * Contains text filter
 * SELECT FROM Class WHERE name CONTAINSTEXT 'text'
 */
public class ContainsTextFilterCriteria extends AbstractFilterCriteria {

    private final IFilterValue value;

    public ContainsTextFilterCriteria(String field, PrimeFilterValue<String> value, IModel<Boolean> join) {
        super(field, FilterCriteriaType.CONTAINS_TEXT + "_" + field, join);
        this.value = value;
    }

    @Override
    protected String apply(String field) {
        List<String> stringList = value.toStringList();
        if (!needToApplyFilter(stringList))
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append(field)
                .append(" CONTAINSTEXT '")
                .append(stringList.get(0))
                .append("'");
        return sb.toString();
    }
}
