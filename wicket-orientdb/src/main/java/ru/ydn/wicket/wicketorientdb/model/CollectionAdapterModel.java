package ru.ydn.wicket.wicketorientdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 * Adapter model to transform various {@link Collection}s to a IModel&lt;List&gt;
 *
 * @param <T> type of the inner collection objects
 * @param <M> type of a collection themself
 */
public class CollectionAdapterModel<T, M extends Collection<T>> extends LoadableDetachableModel<List<T>>
{
	private IModel<M> model;
	
	public CollectionAdapterModel(IModel<M> model) {
		this.model = model;
	}

	@Override
	protected List<T> load() {
		M ret = model.getObject();
		if(ret==null) return null;
		else if(ret instanceof List) return (List<T>)ret;
		else return new ArrayList<T>(ret);
	}
	
	@Override
	public void setObject(List<T> object) {
		setCollection(object);
		super.setObject(object);
	}
	
	protected void setCollection(List<T> object)
	{
		if(object==null) model.setObject(null);
		else 
		{
			M collection = model.getObject();
			if(collection!=null)
			{
				collection.clear();
				collection.addAll(object);
			}
			else
			{
				throw new WicketRuntimeException("Creation of collection is not supported. Please override this method of you need support.");
			}
		}
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		model.detach();
	}

}
