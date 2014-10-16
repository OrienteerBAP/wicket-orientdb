package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;

/**
 * Simple naming model which use {@link Object}.toString() for obtaining resource key. 
 * @param <T>
 */
public class SimpleNamingModel<T> extends AbstractNamingModel<T>
{
	private static final long serialVersionUID = 1L;
	private String prefix;
	
	public SimpleNamingModel(IModel<T> objectModel)
	{
		super(objectModel);
	}

	public SimpleNamingModel(T object)
	{
		super(object);
	}
	
	public SimpleNamingModel(String prefix, IModel<T> objectModel)
	{
		super(objectModel);
		this.prefix = prefix;
	}

	@Override
	public String getResourceKey(T object) {
		String objectStr = object!=null?object.toString():"null";
		return prefix==null?objectStr:prefix+"."+objectStr;
	}

}
