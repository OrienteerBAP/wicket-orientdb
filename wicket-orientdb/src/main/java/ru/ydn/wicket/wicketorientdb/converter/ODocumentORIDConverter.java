package ru.ydn.wicket.wicketorientdb.converter;

import java.io.Serializable;

import com.google.common.base.Converter;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Converter {@link ODocument}&lt;-&gt;{@link ORID}
 */
public class ODocumentORIDConverter extends Converter<ODocument, ORID> implements Serializable
{
	private static final long serialVersionUID = 1L;
	public static final ODocumentORIDConverter INSTANCE = new ODocumentORIDConverter();

	@Override
	protected ORID doForward(ODocument a) {
		return a.getIdentity();
	}

	@Override
	protected ODocument doBackward(ORID b) {
		return b.getRecord();
	}

}
