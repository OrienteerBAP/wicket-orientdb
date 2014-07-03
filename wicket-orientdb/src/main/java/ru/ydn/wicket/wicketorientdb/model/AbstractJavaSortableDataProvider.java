package ru.ydn.wicket.wicketorientdb.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;
import com.orientechnologies.orient.core.metadata.schema.OClass;

public abstract class AbstractJavaSortableDataProvider<T, S> extends SortableDataProvider<T, S>
{
	private IModel<Collection<T>> dataModel;
	
	public AbstractJavaSortableDataProvider(IModel<Collection<T>> dataModel)
	{
		Args.notNull(dataModel, "dataModel");
		this.dataModel = dataModel;
	}

	@Override
	public Iterator<? extends T> iterator(long first, long count) {
		Collection<T> data =dataModel.getObject();
		if(data==null || data.size()==0) return Iterators.emptyIterator();
		Iterator<T> it;
		final SortParam<S> sortParam = getSort();
		final String sortParamAsString = getSortPropertyAsString(sortParam.getProperty());
		if(sortParamAsString!=null)
		{
			Ordering<T> ordering = Ordering.natural().nullsFirst().onResultOf(new Function<T, Comparable<?>>() {

				@Override
				public Comparable<?> apply(T input) {
					Object value = PropertyResolver.getValue(sortParamAsString, input);
					return value instanceof Comparable?(Comparable<?>)value:null;
				}
			});
			if(!sortParam.isAscending()) ordering = ordering.reverse();
			it=ordering.sortedCopy(data).iterator();
		}
		else
		{
			it=data.iterator();
		}
		Iterators.advance(it, (int)first);
		return Iterators.limit(it, (int)count);
	}
	
	protected String getSortPropertyAsString(S param)
	{
		return param!=null?param.toString():null;
	}

	@Override
	public long size() {
		Collection<T> data =dataModel.getObject();
		return data!=null?data.size():0;
	}

	@Override
	public void detach() {
		super.detach();
		dataModel.detach();
	}
	
}
