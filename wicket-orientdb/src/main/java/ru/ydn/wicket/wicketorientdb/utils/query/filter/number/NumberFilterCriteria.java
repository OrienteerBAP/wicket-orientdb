package ru.ydn.wicket.wicketorientdb.utils.query.filter.number;

import org.apache.wicket.WicketRuntimeException;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.AbstractFilterCriteria;
import ru.ydn.wicket.wicketorientdb.utils.query.filter.IFilterCriteria;

/**
 * Class for filtering numbers
 */
public class NumberFilterCriteria extends AbstractFilterCriteria {

    private INumberFilter numberFilter;

    public NumberFilterCriteria(String fieldName) {
        this(fieldName, null);
    }

    public NumberFilterCriteria(String fieldName, INumberFilter numberFilter) {
        super(fieldName);
        this.numberFilter = numberFilter;
    }

    @Override
    protected String apply() {
        if (numberFilter == null)
            throw new WicketRuntimeException("OrientDb number filter not configure!");

        return numberFilter.apply(getFieldName());
    }

    public IFilterCriteria setNumberFilter(INumberFilter numberFilter) {
        this.numberFilter = numberFilter;
        return this;
    }

}
