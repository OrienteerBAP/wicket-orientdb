package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.LoadableDetachableModel;

import ru.ydn.wicket.wicketorientdb.proto.IPrototype;

/**
 * Abstract class for storing {@link IPrototype} and transparent switching to realized object
 * @param <T>
 */
public abstract class PrototypeLoadableDetachableModel<T> extends
		LoadableDetachableModel<T> {

	private static final long serialVersionUID = 1L;
	private IPrototype<T> prototype;
	
	public PrototypeLoadableDetachableModel() {
	}

	public PrototypeLoadableDetachableModel(T object) {
		setObject(object);
	}

	@Override
	protected final T load() {
		if(prototype!=null)
		{
			return prototype.thisPrototype();
		}
		else
		{
			return loadInstance();
		}
	}
	
	
	
	@Override
	public void detach() {
		if(prototype!=null && prototype.isPrototypeRealized())
		{
			setObject(prototype.obtainRealizedObject());
			prototype=null;
		}
		super.detach();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setObject(T object) {
		if(object instanceof IPrototype<?>)
		{
			prototype = (IPrototype<T>)object;
		}
		else
		{
			handleObject(object);
		}
		super.setObject(object);
	}

	/**
	 * Load real object. Invoked if object is definetly real
	 * @return real object
	 */
	protected abstract T loadInstance();
	
	/**
	 * Method for obtaining PK parameters from the object 
	 * @param object
	 */
	protected abstract void handleObject(T object);

}
