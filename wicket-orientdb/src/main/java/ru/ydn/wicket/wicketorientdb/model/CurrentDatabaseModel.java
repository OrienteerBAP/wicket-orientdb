package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;

/**
 * Simple {@link IModel} to return current {@link ODatabase}
 * @param <T> the type of required {@link ODatabase}
 */
public class CurrentDatabaseModel<T extends ODatabase<?>> extends
		AbstractReadOnlyModel<T> {
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
