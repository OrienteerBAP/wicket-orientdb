package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

/**
 * Simple {@link IModel} to return current {@link ODatabase}
 * @param <T> the type of required {@link ODatabase}
 */
public class CurrentDatabaseModel<T extends ODatabase<?>> implements IModel<T> {
	private static final long serialVersionUID = 1L;
	private static final CurrentDatabaseModel<ODatabaseDocument> INSTANCE = new CurrentDatabaseModel<ODatabaseDocument>();
	
	@SuppressWarnings("unchecked")
	public static <D extends ODatabase<?>> CurrentDatabaseModel<D> getInstance() {
		return (CurrentDatabaseModel<D>)INSTANCE;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T getObject() {
		return (T)OrientDbWebSession.get().getDatabase();
	}

}
