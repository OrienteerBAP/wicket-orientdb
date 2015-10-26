package ru.ydn.wicket.wicketorientdb.proto;

import org.apache.wicket.util.io.IClusterable;

/**
 * Listener to be executed when object created
 *
 * @param <T> the type of a prototype
 */
public interface IPrototypeListener<T> extends IClusterable {
	
	public void onRealizePrototype(IPrototype<T> prototype);
	
}
