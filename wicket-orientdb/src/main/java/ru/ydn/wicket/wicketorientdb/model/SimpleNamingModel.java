package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;

public class SimpleNamingModel extends AbstractNamingModel<String>
{
	private String prefix;
	
	public SimpleNamingModel(IModel<String> objectModel)
	{
		super(objectModel);
	}

	public SimpleNamingModel(String object)
	{
		super(object);
	}
	
	public SimpleNamingModel(String prefix, IModel<String> objectModel)
	{
		super(objectModel);
		this.prefix = prefix;
	}

	@Override
	public String getResourceKey(String object) {
		return prefix==null?object:prefix+"."+object;
	}

}
