package ru.ydn.wicket.wicketorientdb.proto;

import java.io.Serializable;

public interface IPrototype<T> extends Serializable
{
	public boolean isPrototypeRealized();
	public T realizePrototype();
	public T obtainRealizedObject();
	public T thisPrototype();
}
