package ru.ydn.wicket.wicketorientdb.converter;

import java.io.Serializable;

import org.danekja.java.util.function.serializable.SerializableFunction;

import com.google.common.base.Converter;

/**
 * Abstract serializable Converter 
 * @param <F> type to convert from
 * @param <T> type to convert to
 */
public abstract class SerializableConverter<F, T> extends Converter<F, T> implements Serializable, SerializableFunction<F, T> {

	/**
	 * Utility method to construct converter from 2 functions
	 * @param <F> type to convert from
     * @param <T> type to convert to
	 * @param to function to convert forward
	 * @param from function to convert backward
	 * @return converter
	 */
	public static <F, T> SerializableConverter<F, T> of(final SerializableFunction<? super F, ? extends T> to, final SerializableFunction<? super T, ? extends F> from) {
		return new SerializableConverter<F, T>() {

			@Override
			protected T doForward(F a) {
				return to.apply(a);
			}

			@Override
			protected F doBackward(T b) {
				return from.apply(b);
			}
		};
	}
}
