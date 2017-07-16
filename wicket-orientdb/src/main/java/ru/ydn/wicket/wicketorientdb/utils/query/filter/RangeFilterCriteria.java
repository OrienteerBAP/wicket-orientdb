package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.base.Strings;
import org.apache.wicket.model.IModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.IFilterValue;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.CollectionFilterValue;

import java.util.List;

/**
 * Range filter
 * SELECT FROM Class WHERE num BETWEEN '1' AND '5'
 */
public class RangeFilterCriteria extends AbstractFilterCriteria {

    private final IFilterValue filterValue;

    public <T> RangeFilterCriteria(String field, CollectionFilterValue<T> filterValue, IModel<Boolean> join) {
        super(field, FilterCriteriaType.RANGE.getName() + field, join);
        this.filterValue = filterValue;
    }

    @Override
    protected String apply(String field) {
        List<String> stringList = filterValue.toStringList();
        if (!needToApplyFilter(stringList))
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append(field);
        sb.append(" BETWEEN ");
        sb.append(stringList.get(0));
        sb.append(" AND ");
        sb.append(stringList.get(1));
        return sb.toString();
    }

    @Override
    protected boolean needToApplyFilter(List<String> stringList) {
        return super.needToApplyFilter(stringList) && stringList.size() == 2
                && !Strings.isNullOrEmpty(stringList.get(0))
                && !Strings.isNullOrEmpty(stringList.get(1));
    }
}
