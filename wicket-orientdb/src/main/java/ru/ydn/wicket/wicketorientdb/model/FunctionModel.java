package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;

import com.google.common.base.Converter;
import com.google.common.base.Function;

/**
 * Model for lazy apply of function on object from underling model
 * @param <F>
 * @param <T>
 */
public class FunctionModel<F, T> extends AbstractConverterModel<F, T>
{
	private static final long serialVersionUID = 1L;
	private Function<? super F, ? extends T> function;
	
	public FunctionModel(IModel<F> fromModel, Function<? super F, ? extends T> function) {
		super(fromModel);
		Args.notNull(function, "Function should be specified");
		this.function = function;
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
