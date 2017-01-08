package ru.ydn.wicket.wicketorientdb.utils;

import java.io.Serializable;

import org.apache.wicket.model.IModel;

import com.google.common.base.Function;

/**
 * Utility {@link Function} for obtaining object from specified {@link IModel}
 * @param <T> type of model
 */
public class GetObjectFunction<T> implements Function<IModel<T>, T>, Serializable
{
	private static final long serialVersionUID = 1L;
	public static final GetObjectFunction<?> INSTANCE = new GetObjectFunction<Object>();
	
	@Override
	public T apply(IModel<T> input) {
		return input.getObject();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> GetObjectFunction<T> getInstance()
	{
		return (GetObjectFunction<T>)INSTANCE;
	}

}
