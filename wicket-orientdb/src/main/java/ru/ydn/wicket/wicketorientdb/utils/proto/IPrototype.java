package ru.ydn.wicket.wicketorientdb.utils.proto;

import java.io.Serializable;

public interface IPrototype<T> extends Serializable
{
	public boolean isPrototypeRealized();
	public T realizePrototype();
	public T obtainRealizedPrototype();
}
