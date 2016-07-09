package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;

import com.google.common.base.Converter;
import com.google.common.base.Function;

/**
 * Model for lazy apply of function on object from underling model
 * @param <F>
 * @param <T>
 */
public abstract class AbstractConverterModel <F, T> extends Converter<F, T> implements IModel<T> {
	private static final long serialVersionUID = 1L;
	protected IModel<F> fromModel;
	
	public AbstractConverterModel(IModel<F> fromModel) {
		this.fromModel = fromModel;
	}
	
	@Override
	public T getObject() {
		return doForward(fromModel.getObject());
	}

	@Override
	public void setObject(T object) {
		fromModel.setObject(doBackward(object));
	}
	
	@Override
	protected F doBackward(T b) {
		throw new WicketRuntimeException("Backward convertion is not supported");
	}
	
	@Override
	public void detach() {
		fromModel.detach();
	}


	@Override
	public String toString() {
		return "AbstractConverterModel [fromModel=" + fromModel + "]";
	}
}
