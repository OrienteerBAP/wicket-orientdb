package ru.ydn.wicket.wicketorientdb.converter;

import java.io.Serializable;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.google.common.base.Converter;
import com.orientechnologies.orient.core.index.OIndex;

/**
 * Converter {@link OIndex}&lt;-&gt;{@link String}
 */
public class OIndexNameConverter  extends AbstractJointConverter<OIndex> implements Serializable
{
	private static final long serialVersionUID = 1L;
	public static final OIndexNameConverter INSTANCE = new OIndexNameConverter();

	@Override
	public OIndex convertToObject(String value, Locale locale) throws ConversionException {
		return OrientDbWebSession.get().getDatabase().getMetadata().getIndexManager().getIndex(value);
	}

	@Override
	public String convertToString(OIndex value, Locale locale) {
		return value.getName();
	}

}
