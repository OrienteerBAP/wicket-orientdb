package ru.ydn.wicket.wicketorientdb.utils;

import java.io.Serializable;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.google.common.base.Converter;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class OIndexNameConverter  extends Converter<OIndex<?>, String> implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final OIndexNameConverter INSTANCE = new OIndexNameConverter();

	@Override
	protected String doForward(OIndex<?> a) {
		return a.getName();
	}

	@Override
	protected OIndex<?> doBackward(String b) {
		return OrientDbWebSession.get().getDatabase().getMetadata().getIndexManager().getIndex(b);
	}

}
