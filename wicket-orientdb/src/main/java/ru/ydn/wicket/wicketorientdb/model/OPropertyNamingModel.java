package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class OPropertyNamingModel extends AbstractNamingModel<OProperty> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OPropertyNamingModel(OProperty oProperty)
	{
		super(oProperty);
	}

	public OPropertyNamingModel(IModel<OProperty> objectModel) {
		super(objectModel);
	}

	@Override
	public String getResourceKey(OProperty object) {
		return object.getFullName();
	}

}
