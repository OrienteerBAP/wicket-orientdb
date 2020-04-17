package ru.ydn.wicket.wicketorientdb.model;

import java.io.Serializable;
import java.util.Collection;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * {@link AbstractJavaSortableDataProvider} for {@link Serializable} objects
 *
 * @param <T> type of entities provided by this {@link IDataProvider} 
 * @param <S> the type of the sorting parameter
 */
public class JavaSortableDataProvider<T extends Serializable, S> extends
		AbstractJavaSortableDataProvider<T, S> {
	
	private static final long serialVersionUID = 1L;

	public JavaSortableDataProvider(IModel<? extends Collection<T>> dataModel) {
		super(dataModel);
	}

	@Override
	public IModel<T> model(T object) {
		return Model.of(object);
	}

}
