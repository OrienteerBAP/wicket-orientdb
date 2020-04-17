package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;

/**
 * Abstract filtered provider which indicates that provider support filtering or no.
 * @param <K> data type
 * @param <S> sort type
 */
public abstract class AbstractFilteredProvider<K, S> extends SortableDataProvider<K, S>
        implements IFilterStateLocator<OQueryModel<K>> {

	private static final long serialVersionUID = 1L;

	public boolean isFilterEnable() {
        return false;
    }

    @Override
    public OQueryModel<K> getFilterState() {
        return null;
    }

    @Override
    public final void setFilterState(OQueryModel<K> state) {
        throw new UnsupportedOperationException("For change filter state, please see class OQueryModel");
    }
}
