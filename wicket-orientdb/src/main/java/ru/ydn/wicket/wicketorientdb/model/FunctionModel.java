package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;

import com.google.common.base.Converter;
import com.google.common.base.Function;

public class FunctionModel<F, T> implements IModel<T>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IModel<F> fromModel;
	private Function<? super F, ? extends T> function;
	
	public FunctionModel(IModel<F> fromModel, Function<? super F, ? extends T> function) {
		this.fromModel = fromModel;
		this.function = function;
	}


	@Override
	public T getObject() {
		return function.apply(fromModel.getObject());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setObject(T object) {
		if(function instanceof Converter)
		{
			Converter<F, T> converter = (Converter<F, T>)function;
			F fromtoSet = converter.reverse().convert(object);
			fromModel.setObject(fromtoSet);
		}
		else
		{
			throw new WicketRuntimeException("Can't set object for model: "+this);
		}
	}
	
	@Override
	public void detach() {
		fromModel.detach();
	}


	@Override
	public String toString() {
		return "FunctionModel [fromModel=" + fromModel + ", function="
				+ function + "]";
	}

}
