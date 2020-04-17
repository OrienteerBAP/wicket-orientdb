package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

/**
 * Model for storing {@link OProperty} instance
 */
public class OPropertyModel extends PrototypeLoadableDetachableModel<OProperty>
{
	private static final long serialVersionUID = 1L;
	private IModel<OClass> classModel;
	private String propertyName;
	
	public OPropertyModel(OProperty oProperty)
	{
		super(oProperty);
	}
	
	
	public OPropertyModel(String className, String propertyName)
	{
		this(new OClassModel(className), propertyName);
	}
	
	public OPropertyModel(IModel<OClass> classModel, String propertyName)
	{
		this.classModel = classModel;
		this.propertyName = propertyName;
	}

	@Override
	protected OProperty loadInstance() {
			OClass oClass = classModel!=null?classModel.getObject():null;
			return oClass!=null && propertyName!=null?oClass.getProperty(propertyName):null;
	}
	

	@Override
	protected void handleObject(OProperty object) {
		if(object!=null)
		{
			classModel = new OClassModel(object.getOwnerClass());
			propertyName = object.getName();
		}
		else
		{
			classModel=null;
			propertyName=null;
		}
	}
	
	@Override
	protected void onDetach() {
		if(classModel!=null)
		{
			OProperty property = getObject();
			if(property!=null && !property.getName().equals(propertyName))
			{
				propertyName=property.getName();
			}
			classModel.detach();
		}
	}


	public OSchema getSchema()
	{
		return getDatabase().getMetadata().getSchema();
	}
	
	public ODatabaseDocument getDatabase()
	{
		return OrientDbWebSession.get().getDatabase();
	}


	@Override
	public Class<OProperty> getObjectClass() {
		return OProperty.class;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((classModel == null) ? 0 : classModel.hashCode());
		result = prime * result
				+ ((propertyName == null) ? 0 : propertyName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OPropertyModel other = (OPropertyModel) obj;
		if (classModel == null) {
			if (other.classModel != null)
				return false;
		} else if (!classModel.equals(other.classModel))
			return false;
		if (propertyName == null) {
			if (other.propertyName != null)
				return false;
		} else if (!propertyName.equals(other.propertyName))
			return false;
		return true;
	}
	
}
