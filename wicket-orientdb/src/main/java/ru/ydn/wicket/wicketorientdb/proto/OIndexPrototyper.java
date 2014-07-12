package ru.ydn.wicket.wicketorientdb.proto;

import com.orientechnologies.orient.core.index.OIndex;

public class OIndexPrototyper extends AbstractPrototyper<OIndex<?>>
{
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String DEF = "definition.fields";
	public static final String DEF_FIELDS = "definition.fields";
	public static final String DEF_FILED_TO_INDEX = "definition.fieldsToIndex";
	public static final String DEF_COLLATE = "definition.collate";
	public static final String DEF_NULLS_IGNORED = "definition.nullValuesIgnored";
	public static final String SIZE = "size";
	public static final String KEY_SIZE = "keySize";

	public static final String[] OINDEX_ATTRS = new String[]{NAME, TYPE, DEF_FIELDS, DEF_FILED_TO_INDEX, DEF_COLLATE, DEF_NULLS_IGNORED, SIZE, KEY_SIZE};

	@Override
	protected OIndex<?> createInstance(OIndex<?> proxy) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Class<OIndex<?>> getMainInterface() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
