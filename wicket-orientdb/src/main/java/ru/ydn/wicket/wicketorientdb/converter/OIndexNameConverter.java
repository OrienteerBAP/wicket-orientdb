package ru.ydn.wicket.wicketorientdb.converter;

import java.io.Serializable;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.google.common.base.Converter;
import com.orientechnologies.orient.core.index.OIndex;

/**
 * Converter {@link OIndex}&lt;-&gt;{@link String}
 */
public class OIndexNameConverter  extends Converter<OIndex<?>, String> implements Serializable
{
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
