package ru.ydn.wicket.wicketorientdb.proto;

import java.io.Serializable;

/**
 * Interface for manipulations on prototype
 * @param <T>
 */
public interface IPrototype<T> extends Serializable
{
	public boolean isPrototypeRealized();
	public T realizePrototype();
	public T obtainRealizedObject();
	public T thisPrototype();
}
