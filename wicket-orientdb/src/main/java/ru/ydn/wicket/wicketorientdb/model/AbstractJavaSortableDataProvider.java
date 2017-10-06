package ru.ydn.wicket.wicketorientdb.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;

/**
 * Realization of {@link SortableDataProvider} which use properties values and in memory sorting
 * @param <T> type of entities provided by this {@link IDataProvider}
 * @param <S> the type of the sorting parameter
 */
public abstract class AbstractJavaSortableDataProvider<T, S> extends AbstractFilteredProvider<T, S>
{
	private static final long serialVersionUID = 1L;
	private final IModel<? extends Collection<T>> dataModel;
	private final Predicate<? super T> filterPredicate;
	
	public AbstractJavaSortableDataProvider(IModel<? extends Collection<T>> dataModel)
	{
		this(dataModel, null);
	}
	
	public AbstractJavaSortableDataProvider(IModel<? extends Collection<T>> dataModel, Predicate<? super T> filterPredicate)
	{
		Args.notNull(dataModel, "dataModel");
		this.dataModel = dataModel;
		this.filterPredicate = filterPredicate;
	}

	@Override
	public Iterator<? extends T> iterator(long first, long count) {
		Collection<T> data =dataModel.getObject();
		if(data==null || data.size()==0) return Collections.emptyIterator();
		if(filterPredicate!=null) data = Collections2.filter(data, filterPredicate); 
		Iterator<T> it;
		final SortParam<S> sortParam = getSort();
		if(sortParam!=null && sortParam.getProperty()!=null)
		{
			Ordering<T> ordering = Ordering.natural().nullsFirst().onResultOf(new Function<T, Comparable<?>>() {

				@Override
				public Comparable<?> apply(T input) {
					return comparableValue(input, sortParam.getProperty());
				}
			});
			if(!sortParam.isAscending()) ordering = ordering.reverse();
			it=ordering.sortedCopy(data).iterator();
		}
		else
		{
			it=data.iterator();
		}
		if(filterPredicate!=null) it = Iterators.filter(it, filterPredicate);
		if(first>0) Iterators.advance(it, (int)first);
		return count>=0?Iterators.limit(it, (int)count):it;
	}
	
	protected Comparable<?> comparableValue(T input, S sortParam)
	{
		String property = getSortPropertyExpression(sortParam);
		if(property==null) return null;
		Object value = PropertyResolver.getValue(property, input);
		return value instanceof Comparable?(Comparable<?>)value:null;
	}
	
	protected String getSortPropertyExpression(S param)
	{
		return param!=null?param.toString():null;
	}

	@Override
	public long size() {
		Collection<T> data =dataModel.getObject();
		if(filterPredicate!=null) data= Collections2.filter(data, filterPredicate);
		return data!=null?data.size():0;
	}

	@Override
	public void detach() {
		super.detach();
		dataModel.detach();
	}
	
}
