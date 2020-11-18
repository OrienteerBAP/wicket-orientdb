package ru.ydn.wicket.wicketorientdb.converter;

import java.io.Serializable;

import org.apache.wicket.util.string.Strings;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.google.common.base.Converter;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

/**
 * Converter {@link OIndex}&lt;-&gt;{@link String} (full property name)
 */
public class OPropertyFullNameConverter extends Converter<OProperty, String> implements Serializable
{
	private static final long serialVersionUID = 1L;
	public static final OPropertyFullNameConverter INSTANCE = new OPropertyFullNameConverter();

	@Override
	protected String doForward(OProperty a) {
		return a.getFullName();
	}

	@Override
	protected OProperty doBackward(String b) {
		OSchema schema = OrientDbWebSession.get().getSchema();
		String className = Strings.beforeFirst(b, '.');
		String propertyName = Strings.afterFirst(b, '.');
		OClass oClass = schema.getClass(className);
		return oClass.getProperty(propertyName);
	}

}