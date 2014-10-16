package ru.ydn.wicket.wicketorientdb.converter;

import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Converter for {@link ODocument} &lt;-&gt; {@link String}
 */
public class ODocumentConverter extends OIdentifiableConverter<ODocument>
{
	private static final long serialVersionUID = 1L;

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
