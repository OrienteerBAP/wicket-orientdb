package ru.ydn.wicket.wicketorientdb.converter;

import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;

import com.orientechnologies.orient.core.exception.OSerializationException;
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
		value = value.trim();
		if(value.startsWith("{")) {
			try {
				ODocument doc = new ODocument();
				return doc.fromJSON(value, true);
			} catch (OSerializationException e) {
				throw newConversionException("Cannot convert '" + value + "' to "+getTargetType().getSimpleName()+": "+e.getMessage(),
													value, locale);
			}
		}
		else return convertToOIdentifiable(value, locale).getRecord();
	}
	
	@Override
	public String convertToString(ODocument value, Locale locale) {
		return value.getIdentity().isPersistent() ? super.convertToString(value, locale):value.toJSON(); 
	}

	@Override
	protected Class<ODocument> getTargetType() {
		return ODocument.class;
	}

}
