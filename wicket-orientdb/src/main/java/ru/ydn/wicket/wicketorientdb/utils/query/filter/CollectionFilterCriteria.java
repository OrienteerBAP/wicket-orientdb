package ru.ydn.wicket.wicketorientdb.utils.query.filter;

import com.google.common.base.Strings;
import org.apache.wicket.model.IModel;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.IFilterValue;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.value.CollectionFilterValue;

import java.util.List;

/**
 * Collection filter
 * SELECT FROM Class WHERE num IN ['1', '2', '3']
 */
public class CollectionFilterCriteria extends AbstractFilterCriteria {

    private final IFilterValue filterValue;

    public <T> CollectionFilterCriteria(String field, CollectionFilterValue<T> filterValue, IModel<Boolean> join) {
        super(field, FilterCriteriaType.LIST + "_" + field, join);
        this.filterValue = filterValue;
    }


    @Override
    protected String apply(String field) {
        List<String> stringList = filterValue.toStringList();
        if (!needToApplyFilter(stringList))
            return null;
        StringBuilder sb = new StringBuilder();

        sb.append(field);
        sb.append(" IN [");
        int counter = 0;
        for (String value : stringList) {
            if (!Strings.isNullOrEmpty(value)) {
                if (counter > 0)
                    sb.append(", ");
                sb.append(value);
                counter++;
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
