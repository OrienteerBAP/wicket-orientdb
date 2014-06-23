package ru.ydn.wicket.wicketorientdb.utils;

import org.apache.wicket.model.IModel;

import com.google.common.base.Function;

public class GetObjectFunction<T> implements Function<IModel<T>, T>
{
	public static final GetObjectFunction<Object> INSTANCE = new GetObjectFunction<>();
	
	@Override
	public T apply(IModel<T> input) {
		return input.getObject();
	}

}
