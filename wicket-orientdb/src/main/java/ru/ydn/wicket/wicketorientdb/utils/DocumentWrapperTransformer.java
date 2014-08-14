package ru.ydn.wicket.wicketorientdb.utils;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.wicket.WicketRuntimeException;

import com.google.common.base.Function;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class DocumentWrapperTransformer<T> implements Function<ODocument, T>, Serializable
{
	private final Class<? extends T> wrapperClass;
	private transient Constructor<? extends T> constructor;
	public DocumentWrapperTransformer(Class<? extends T> wrapperClass)
	{
		this.wrapperClass = wrapperClass;
		//To check that appropriate constructor exists
		getConstructor();
	}
	
	private Constructor<? extends T> getConstructor()
	{
		if(constructor==null)
		{
			try
			{
				constructor = wrapperClass.getConstructor(ODocument.class);
			} catch (NoSuchMethodException e)
			{
				throw new WicketRuntimeException("Approapriate constructor was not found. DocumentWrapper class: "+wrapperClass.getName());
			} catch (SecurityException e)
			{
				throw new WicketRuntimeException("Can't get access to constructor of class: "+wrapperClass.getName(), e);
			}
		}
		return constructor;
	}
	
	
	@Override
	public T apply(ODocument input) {
		try
		{
			return getConstructor().newInstance(input);
		} catch (Exception e)
		{
			throw new WicketRuntimeException("Can't create wrapper instance of class '"+wrapperClass.getName()+"' for document: "+input, e);
		} 
	}

	public Class<? extends T> getWrapperClass() {
		return wrapperClass;
	}
	
}
