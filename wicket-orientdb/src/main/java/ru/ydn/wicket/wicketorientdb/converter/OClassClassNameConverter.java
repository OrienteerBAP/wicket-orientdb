package ru.ydn.wicket.wicketorientdb.converter;

import java.io.Serializable;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

/**
 * Converter {@link OClass}&lt;-&gt;{@link String}
 */
public class OClassClassNameConverter extends AbstractJointConverter<OClass> implements Serializable
{
	private static final long serialVersionUID = 1L;
	public static final OClassClassNameConverter INSTANCE = new OClassClassNameConverter();

	@Override
	public OClass convertToObject(String value, Locale locale) throws ConversionException {
		return OrientDbWebSession.get().getSchema().getClass(value);
	}

	@Override
	public String convertToString(OClass value, Locale locale) {
		return value.getName();
	}

}
