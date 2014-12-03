package ru.ydn.wicket.wicketorientdb.model;

import java.util.Collection;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

/**
 * {@link SortableDataProvider} for listing of {@link OProperty}es of specific {@link OClass}
 */
public class OPropertiesDataProvider
		extends
		AbstractJavaSortableDataProvider<OProperty, String> {
	
	private static final long serialVersionUID = 1L;

	public OPropertiesDataProvider(OClass oClass,  boolean allProperties)
	{
		this(new OClassModel(oClass), Model.<Boolean>of(allProperties));
	}

	public OPropertiesDataProvider(final IModel<OClass> oClassModel, final IModel<Boolean> allPropertiesModel) {
		super(new ListOPropertiesModel(oClassModel, allPropertiesModel));
	}
	
	public OPropertiesDataProvider(IModel<Collection<OProperty>> dataModel) {
		super(dataModel);
	}

	@Override
	public IModel<OProperty> model(OProperty object) {
		return new OPropertyModel(object);
	}

}
