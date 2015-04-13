package ru.ydn.wicket.wicketorientdb.converter;

import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.converter.AbstractConverter;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;

/**
 * Converter for {@link OIdentifiable} &lt;-&gt; {@link String}
 * Can be overridden for subclasses of {@link OIdentifiable}
 * @param <T>
 */
public class OIdentifiableConverter<T extends OIdentifiable> extends AbstractConverter<T>
{

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public T convertToObject(String value, Locale locale)
			throws ConversionException {
		return (T) convertToOIdentifiable(value, locale);
	}
	
	/**
	 * Converts string to {@link ORecordId}
	 * @param value string representation of a {@link ORID}
	 * @param locale locale
	 * @return {@link ORecordId} for a specified rid
	 */
	public OIdentifiable convertToOIdentifiable(String value, Locale locale)
	{
		try
		{
			return new ORecordId(value);
		} catch (Exception e)
		{
			throw newConversionException("Cannot convert '" + value + "' to "+getTargetType().getSimpleName(), value, locale);
		}
	}
	
	@Override
	public String convertToString(T value, Locale locale) {
		return value.getIdentity().toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Class<T> getTargetType() {
		return (Class<T>) OIdentifiable.class;
	}

}
