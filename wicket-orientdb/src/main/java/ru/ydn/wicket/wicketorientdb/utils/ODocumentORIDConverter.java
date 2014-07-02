package ru.ydn.wicket.wicketorientdb.utils;

import java.io.Serializable;

import com.google.common.base.Converter;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODocumentORIDConverter extends Converter<ODocument, ORID> implements Serializable
{
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
