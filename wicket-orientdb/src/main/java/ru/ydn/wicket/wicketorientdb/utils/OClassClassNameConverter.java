package ru.ydn.wicket.wicketorientdb.utils;

import java.io.Serializable;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.google.common.base.Converter;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

public class OClassClassNameConverter extends Converter<OClass, String> implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final OClassClassNameConverter INSTANCE = new OClassClassNameConverter();

	@Override
	protected String doForward(OClass a) {
		return a.getName();
	}

	@Override
	protected OClass doBackward(String b) {
		ODatabaseRecord db = OrientDbWebSession.get().getDatabase();
		OSchema schema = db.getMetadata().getSchema();
		return schema.getClass(b);
	}

}
