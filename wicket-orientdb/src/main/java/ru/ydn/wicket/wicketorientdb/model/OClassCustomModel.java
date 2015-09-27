package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * {@link IModel} to get and set dynamically custom properties of an {@link OClass}
 */
public class OClassCustomModel extends AbstractCustomValueModel<OClass, String, String> {

	public OClassCustomModel(IModel<OClass> mainObjectModel,
			IModel<String> propertyModel) {
		super(mainObjectModel, propertyModel);
	}

	@Override
	public Class<String> getObjectClass() {
		return String.class;
	}

	@Override
	protected String getValue(OClass object, String param) {
		return object.getCustom(param);
	}

	@Override
	protected void setValue(OClass object, String param, String value) {
		object.setCustom(param, value);
	}

}
