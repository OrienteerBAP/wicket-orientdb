package ru.ydn.wicket.wicketorientdb.model;

import java.util.Collection;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;

public class OIndexiesDataProvider extends
		AbstractJavaSortableDataProvider<OIndex<?>, String>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OIndexiesDataProvider(OClass oClass, boolean allIndexes)
	{
		this(new OClassModel(oClass), Model.<Boolean> of(allIndexes));
	}

	public OIndexiesDataProvider(final IModel<OClass> oClassModel,
			final IModel<Boolean> allIndexiesModel)
	{
		super(new LoadableDetachableModel<Collection<OIndex<?>>>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected Collection<OIndex<?>> load() {
				if (Boolean.TRUE.equals(allIndexiesModel.getObject()))
				{
					return oClassModel.getObject().getIndexes();
				} else
				{
					return oClassModel.getObject().getClassIndexes();
				}
			}

			@Override
			public void detach() {
				super.detach();
				oClassModel.detach();
				allIndexiesModel.detach();
			}

		});
	}

	public OIndexiesDataProvider(IModel<Collection<OIndex<?>>> dataModel) {
		super(dataModel);
	}

	@Override
	public IModel<OIndex<?>> model(OIndex<?> object) {
		return new OIndexModel(object);
	}

}
