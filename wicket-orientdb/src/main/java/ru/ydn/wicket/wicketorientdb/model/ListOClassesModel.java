package ru.ydn.wicket.wicketorientdb.model;

import java.util.Collection;

import org.apache.wicket.model.IModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * {@link IModel} to list all {@link OClass}es
 */
public class ListOClassesModel extends AbstractListModel<OClass>
{
	
	private static final long serialVersionUID = 1L;

	@Override
	protected Collection<OClass> getData() {
		return OrientDbWebSession.get().getSchema().getClasses();
	}

}
