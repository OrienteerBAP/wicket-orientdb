package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;

public class SimpleNamingModel<T> extends AbstractNamingModel<T>
{
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
