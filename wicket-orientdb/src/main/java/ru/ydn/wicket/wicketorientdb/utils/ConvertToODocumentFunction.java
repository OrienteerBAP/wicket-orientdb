package ru.ydn.wicket.wicketorientdb.utils;

import org.apache.wicket.WicketRuntimeException;

import com.google.common.base.Function;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ConvertToODocumentFunction<F> implements Function<F, ODocument>
{
	public static final ConvertToODocumentFunction<?> INSTANCE = new ConvertToODocumentFunction<Object>();
	
	@Override
	public ODocument apply(F input) {
		if(input==null)
		{
			return null;
		}
		else if(input instanceof ODocument)
		{
			return (ODocument)input;
		}
		else if(input instanceof ORID)
		{
			return ((ORID)input).getRecord();
		}
		else if(input instanceof CharSequence)
		{
			return new ORecordId(input.toString()).getRecord();
		}
		else
		{
			throw new WicketRuntimeException("Object '"+input+"' of type '"+input.getClass()+"' can't be converted to ODocument");
		}
	}
	

}
