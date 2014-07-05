package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class OPropertyModel extends LoadableDetachableModel<OProperty>
{
	private IModel<OClass> classModel;
	private IModel<String> propertyNameModel;
	
	public OPropertyModel(OProperty oProperty)
	{
		this(oProperty.getOwnerClass(), oProperty);
	}
	
	public OPropertyModel(OClass oClass, OProperty oProperty)
	{
		this(new OClassModel(oClass), oProperty.getName());
	}
	
	public OPropertyModel(IModel<OClass> classModel, IModel<String> propertyNameModel)
	{
		this.classModel = classModel;
		this.propertyNameModel = propertyNameModel;
	}
	
	public OPropertyModel(String className, String propertyName)
	{
		this(new OClassModel(className), propertyName);
	}
	
	public OPropertyModel(IModel<OClass> classModel, String propertyName)
	{
		this.classModel = classModel;
		this.propertyNameModel = Model.of(propertyName);
	}

	@Override
	protected OProperty load() {
		OClass oClass = classModel.getObject();
		String property = propertyNameModel.getObject();
		return oClass!=null && property!=null?oClass.getProperty(property):null;
	}
	
	
}
