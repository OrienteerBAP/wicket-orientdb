package ru.ydn.wicket.wicketorientdb.converter;

import java.io.Serializable;

import com.google.common.base.Converter;

/**
 * Serializable identity converter 
 * @param <T> - type of object to convert
 */
public class IdentityConverter<T extends Serializable> extends Converter<T, T> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	protected T doForward(T a) {
		return a;
	}

	@Override
	protected T doBackward(T b) {
		return b;
	}
	
	@Override
	public Converter<T, T> reverse() {
		return this;
	}

}
