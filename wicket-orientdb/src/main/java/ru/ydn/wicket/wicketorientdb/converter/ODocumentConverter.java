package ru.ydn.wicket.wicketorientdb.converter;

import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODocumentConverter extends OIdentifiableConverter<ODocument>
{
	@Override
	public ODocument convertToObject(String value, Locale locale)
			throws ConversionException {
		return convertToOIdentifiable(value, locale).getRecord();
	}

	@Override
	protected Class<ODocument> getTargetType() {
		return ODocument.class;
	}

}
