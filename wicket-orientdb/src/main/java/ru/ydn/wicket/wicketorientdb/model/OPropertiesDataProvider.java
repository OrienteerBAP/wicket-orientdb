package ru.ydn.wicket.wicketorientdb.model;

import java.util.Collection;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class OPropertiesDataProvider
		extends
		AbstractJavaSortableDataProvider<OProperty, String> {
	
	public OPropertiesDataProvider(OClass oClass,  boolean allProperties)
	{
		this(new OClassModel(oClass), Model.<Boolean>of(allProperties));
	}

	public OPropertiesDataProvider(final IModel<OClass> oClassModel, final IModel<Boolean> allPropertiesModel) {
		super(new LoadableDetachableModel<Collection<OProperty>>() {

			@Override
			protected Collection<OProperty> load() {
				if(Boolean.TRUE.equals(allPropertiesModel.getObject()))
				{
					return oClassModel.getObject().properties();
				}
				else
				{
					return oClassModel.getObject().declaredProperties();
				}
			}

			@Override
			public void detach() {
				super.detach();
				oClassModel.detach();
				allPropertiesModel.detach();
			}
			
		});
	}
	
	public OPropertiesDataProvider(IModel<Collection<OProperty>> dataModel) {
		super(dataModel);
	}

	@Override
	public IModel<OProperty> model(OProperty object) {
		return new OPropertyModel(object);
	}

}
