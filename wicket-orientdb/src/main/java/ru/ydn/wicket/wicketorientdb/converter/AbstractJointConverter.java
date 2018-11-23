package ru.ydn.wicket.wicketorientdb.converter;

import java.io.Serializable;
import java.util.Locale;

import org.apache.wicket.Session;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.danekja.java.util.function.serializable.SerializableFunction;

import com.google.common.base.Converter;

/**
 * Class which union 2 converters: {@link Converter} and {@link IConverter} 
 * @param <F>
 */
public abstract class AbstractJointConverter<F> extends Converter<F, String> implements IConverter<F>, Serializable{

	@Override
	protected String doForward(F a) {
		return convertToString(a, Session.exists()?Session.get().getLocale():Locale.getDefault());
	}

	@Override
	protected F doBackward(String b) {
		return convertToObject(b, Session.exists()?Session.get().getLocale():Locale.getDefault());
	}
	
	protected ConversionException newConversionException(final String message, 
														final Object value, 
														final Class<?> targetType,
														final Locale locale)
	{
			return new ConversionException(message).setSourceValue(value)
				.setTargetType(targetType)
				.setConverter(this)
				.setLocale(locale);
	}
	
	/**
	 * Utility method to construct converter from 2 functions
	 * @param <F> type to convert from
	 * @param to function to convert to String
	 * @param from function to convert from String
	 * @return converter
	 */
	public static <F> AbstractJointConverter<F> of(final SerializableFunction<? super F, String> to,
			                                    final SerializableFunction<String, ? extends F> from) {
		return new AbstractJointConverter<F>() {
			@Override
			public String convertToString(F value, Locale locale) {
				return to.apply(value);
			}
			
			@Override
			public F convertToObject(String value, Locale locale) throws ConversionException {
				return from.apply(value);
			}
		};
	}

}
