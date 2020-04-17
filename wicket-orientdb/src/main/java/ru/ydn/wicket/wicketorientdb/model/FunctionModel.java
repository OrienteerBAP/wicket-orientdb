package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.danekja.java.util.function.serializable.SerializableFunction;

import com.google.common.base.Converter;

import ru.ydn.wicket.wicketorientdb.converter.SerializableConverter;

/**
 * Model for lazy apply of function on object from underling model
 * @param <F> convert from type
 * @param <T> convert to type
 */
public class FunctionModel<F, T> extends AbstractConverterModel<F, T>
{
	private static final long serialVersionUID = 1L;
	private SerializableFunction<? super F, ? extends T> function;
	
	public FunctionModel(IModel<F> fromModel, SerializableFunction<? super F, ? extends T> function) {
		super(fromModel);
		Args.notNull(function, "Function should be specified");
		this.function = function;
	}
	
	public FunctionModel(IModel<F> fromModel, SerializableFunction<? super F, ? extends T> function, SerializableFunction<? super T, ? extends F> backwardFunction) {
		super(fromModel);
		Args.notNull(function, "Function should be specified");
		this.function = SerializableConverter.of(function, backwardFunction);
	}
	
	@Override
	protected T doForward(F a) {
		return function.apply(a);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected F doBackward(T b) {
		if(function instanceof Converter)
		{
			Converter<F, T> converter = (Converter<F, T>)function;
			return converter.reverse().convert(b);
		}
		else
		{
			return super.doBackward(b);
		}
		
	}

	@Override
	public String toString() {
		return "FunctionModel [fromModel=" + fromModel + ", function="
				+ function + "]";
	}

}
