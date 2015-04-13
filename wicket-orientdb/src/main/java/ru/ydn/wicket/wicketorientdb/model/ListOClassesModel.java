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

	@Override
	protected Collection<OClass> getData() {
		return OrientDbWebSession.get().getDatabase().getMetadata().getSchema().getClasses();
	}

}
