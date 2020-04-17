package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;

/**
 * {@link IModel} to get and set dynamically custom properties of an {@link OProperty}
 */
public class OPropertyCustomModel extends AbstractCustomValueModel<OProperty, String, String> {

	private static final long serialVersionUID = 1L;

	public OPropertyCustomModel(IModel<OProperty> mainObjectModel,
			IModel<String> propertyModel) {
		super(mainObjectModel, propertyModel);
	}

	@Override
	public Class<String> getObjectClass() {
		return String.class;
	}

	@Override
	protected String getValue(OProperty object, String param) {
		return object.getCustom(param);
	}

	@Override
	protected void setValue(OProperty object, String param, String value) {
		object.setCustom(param, value);
	}

}
