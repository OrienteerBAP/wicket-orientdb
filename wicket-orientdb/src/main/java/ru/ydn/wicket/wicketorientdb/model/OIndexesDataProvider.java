package ru.ydn.wicket.wicketorientdb.model;

import java.util.Collection;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * {@link SortableDataProvider} for listing of {@link OIndex}es
 */
public class OIndexesDataProvider extends
		AbstractJavaSortableDataProvider<OIndex<?>, String>
{

	private static final long serialVersionUID = 1L;

	public OIndexesDataProvider(OClass oClass, boolean allIndexes)
	{
		this(new OClassModel(oClass), Model.<Boolean> of(allIndexes));
	}

	public OIndexesDataProvider(final IModel<OClass> oClassModel,
			final IModel<Boolean> allIndexesModel)
	{
		super(new ListOIndexesModel(oClassModel, allIndexesModel));
	}

	public OIndexesDataProvider(AbstractListModel<OIndex<?>> indexesModel)
	{
		super(indexesModel);
	}

	public OIndexesDataProvider(IModel<Collection<OIndex<?>>> dataModel) {
		super(dataModel);
	}

	@Override
	public IModel<OIndex<?>> model(OIndex<?> object) {
		return new OIndexModel(object);
	}

}
