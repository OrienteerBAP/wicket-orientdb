package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.base.Strings;
import org.apache.wicket.model.IModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.IFilterValue;

import java.util.List;

/**
 * Equals filter
 * SELECT FROM Class WHERE num = '1'
 */
public class EqualsFilterCriteria extends AbstractFilterCriteria {

    private final IFilterValue filterValue;
    private final boolean rid;

    public EqualsFilterCriteria(String field, IFilterValue filterValue, boolean rid, IModel<Boolean> join) {
        super(field, FilterCriteriaType.EQUALS.getName() +"_" + field, join);
        this.filterValue = filterValue;
        this.rid = rid;
    }

    @Override
    protected String apply(String field) {
        List<String> stringList = filterValue.toStringList();
        if (!needToApplyFilter(stringList))
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append(field);
        sb.append(" = ");
        if (!rid) sb.append("'");
        sb.append(stringList.get(0));
        if (!rid) sb.append("'");
        return sb.toString();
    }

    @Override
    protected boolean needToApplyFilter(List<String> stringList) {
        return super.needToApplyFilter(stringList) && !Strings.isNullOrEmpty(stringList.get(0));
    }
}
