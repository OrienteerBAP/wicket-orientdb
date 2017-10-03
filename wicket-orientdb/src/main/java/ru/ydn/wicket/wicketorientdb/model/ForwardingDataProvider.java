package ru.ydn.wicket.wicketorientdb.model;

import java.util.Iterator;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;

/**
 * {@link IDataProvider} which primary delegate invocations to underlying {@link IDataProvider}
 *
 * @param <T> data type
 * @param <S> sort type
 */
public abstract class ForwardingDataProvider<T, S> extends AbstractFilteredProvider<T, S>{

	protected abstract SortableDataProvider<T,S> delegate();

	@Override
	public Iterator<? extends T> iterator(long first, long count) {
		return delegate().iterator(first, count);
	}

	@Override
	public long size() {
		return delegate().size();
	}
	
	@Override
	public void detach() {
		super.detach();
		delegate().detach();
	}

	@Override
	public IModel<T> model(T object) {
		return delegate().model(object);
	}
	
}
