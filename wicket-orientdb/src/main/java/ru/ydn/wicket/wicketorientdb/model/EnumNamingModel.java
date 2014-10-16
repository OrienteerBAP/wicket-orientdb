package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Classes;

/**
 * {@link AbstractNamingModel} for {@link Enum}s
 * @param <T>
 */
public class EnumNamingModel<T extends Enum<T>> extends AbstractNamingModel<T>
{
	private static final long serialVersionUID = 1L;

	public EnumNamingModel(IModel<T> objectModel, boolean dynamic)
	{
		super(objectModel, dynamic);
	}

	public EnumNamingModel(IModel<T> objectModel)
	{
		super(objectModel);
	}

	public EnumNamingModel(T object)
	{
		super(object);
	}

	@Override
	public String getResourceKey(T object) {
		return Classes.simpleName(object.getDeclaringClass()) + '.' + object.name();
	}

}
