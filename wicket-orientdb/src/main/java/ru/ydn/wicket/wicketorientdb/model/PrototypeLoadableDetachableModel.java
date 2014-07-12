package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.LoadableDetachableModel;

import ru.ydn.wicket.wicketorientdb.proto.IPrototype;

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

	protected abstract T loadInstance();
	
	protected abstract void handleObject(T object);

}
