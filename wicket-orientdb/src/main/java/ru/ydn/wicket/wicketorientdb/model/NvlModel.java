package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.ChainingModel;
import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

/**
 * {@link IModel} which can dynamically return default value if needed
 *
 * @param <T> the type of model object
 */
public class NvlModel<T> extends ChainingModel<T>{
	
	private static final long serialVersionUID = 1L;
	private Object defaultValue;
	
	public NvlModel(IModel<T> mainModel, IModel<T> defaultValueModel) {
		super(mainModel);
		this.defaultValue = defaultValueModel;
	}
	
	public NvlModel(IModel<T> mainModel, T defaultValue) {
		super(mainModel);
		this.defaultValue = defaultValue;
	}
	
	@Override
	public T getObject() {
		T value = super.getObject();
		if(isCondition(value)) return value;
		else return getDefaultObject();
 	}
	
	public boolean isCondition(T value) {
		return value!=null;
	}
	
	/**
	 * Returns default value
	 * @return default value
	 */
	@SuppressWarnings("unchecked")
	public T getDefaultObject() {
		if(defaultValue instanceof IModel) {
			return ((IModel<T>)defaultValue).getObject();
		} else {
			return (T) defaultValue;
		}
	}
	
	@Override
	public void detach()
	{
		super.detach();
		// Detach nested object if it's a detachable
		if (defaultValue instanceof IDetachable)
		{
			((IDetachable)defaultValue).detach();
		}
	}
}
