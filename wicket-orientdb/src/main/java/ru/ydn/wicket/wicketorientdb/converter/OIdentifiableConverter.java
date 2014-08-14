package ru.ydn.wicket.wicketorientdb.converter;

import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.AbstractConverter;
import org.apache.wicket.util.convert.converter.BooleanConverter;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;

public class OIdentifiableConverter<T extends OIdentifiable> extends AbstractConverter<T>
{

	@SuppressWarnings("unchecked")
	@Override
	public T convertToObject(String value, Locale locale)
			throws ConversionException {
		return (T) convertToOIdentifiable(value, locale);
	}
	
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

	@SuppressWarnings("unchecked")
	@Override
	protected Class<T> getTargetType() {
		return (Class<T>) OIdentifiable.class;
	}

}
