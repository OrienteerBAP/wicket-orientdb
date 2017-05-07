package ru.ydn.wicket.wicketorientdb.filter;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.util.lang.Args;

/**
 * @author Vitaliy Gonchar
 * @param <K> The provider object type
 */
public abstract class AbstractFilteredDataProvider<K> extends SortableDataProvider<K, String>
        implements IFilterStateLocator<IODataFilter<K, String>> {
    private IODataFilter<K, String> dataFilter;

    @Override
    public IODataFilter<K, String> getFilterState() {
        return dataFilter;
    }

    /**
     * Set data filter for gets filtered values from OrientDB
     * @param dataFilter {@link IODataFilter} for filter data
     */
    @Override
    public void setFilterState(IODataFilter<K, String> dataFilter) {
        Args.notNull(dataFilter, "dataFilter");
        this.dataFilter = dataFilter;
        configureDataFilter(dataFilter);
    }

    protected abstract void configureDataFilter(IODataFilter<K, String> dataFilter);
}
