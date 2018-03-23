package ru.ydn.wicket.wicketorientdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.model.LoadableDetachableModel;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * Abstract Model for more convenient work with lists
 *
 * @param <T> type of an inner objects
 */
public abstract class AbstractListModel<T> extends LoadableDetachableModel<List<T>>
{

	@Override
	public List<T> load() {
		Collection<T> data = getData();
		if(data==null || data.size()==0) return new ArrayList<T>();
		else
		{
			data = filter(data);
			if(data==null || data.size()==0) return new ArrayList<T>();
			else return order(data);
		}
	}
	
	protected abstract Collection<T> getData();
	
	protected Collection<T> filter(Collection<T> data)
	{
		Predicate<? super T> predicate = getFilterPredicate();
		if(predicate==null) return data;
		else return Collections2.filter(data, predicate);
	}
	
	protected List<T> order(Collection<T> data)
	{
		List<T> list = data instanceof List?(List<T>)data:new ArrayList<T>(data);
		Comparator<? super T> comparator = getComparator();
		if(comparator==null) return list;
		Collections.sort(list, comparator);
		return list;
	}
	
	protected Predicate<? super T> getFilterPredicate()
	{
		return null;
	}
	
	protected Comparator<? super T> getComparator()
	{
		return null;
	}
	

}
