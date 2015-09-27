package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IObjectClassAwareModel;

/**
 * Utility model to customly get and set value to a specified object and parameter
 *
 * @param <T> the type of an object
 * @param <C> the type of an parameter
 * @param <V> the type of an value
 */
public abstract class AbstractCustomValueModel<T, C, V> implements IModel<V>, IObjectClassAwareModel<V>{

	private IModel<T> objectModel;
	private IModel<C> parameterModel;
	public AbstractCustomValueModel(IModel<T> mainObjectModel, IModel<C> propertyModel) {
		this.objectModel = mainObjectModel;
		this.parameterModel = propertyModel;
	}

	@Override
	public V getObject() {
		return getValue(objectModel.getObject(), parameterModel.getObject());
	}

	@Override
	public void setObject(V object) {
		setValue(objectModel.getObject(), parameterModel.getObject(), object);
	}
	
	public IModel<T> getObjectModel() {
		return objectModel;
	}

	public IModel<C> getParameterModel() {
		return parameterModel;
	}

	protected abstract V getValue(T object, C param);
	protected abstract void setValue(T object, C param, V value);

	@Override
	public void detach() {
		parameterModel.detach();
		objectModel.detach();
	}
}
