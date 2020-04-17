package ru.ydn.wicket.wicketorientdb.model;

import java.util.Objects;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

/**
 * Model for storing {@link OClass} instance
 */
public class OClassModel extends PrototypeLoadableDetachableModel<OClass>{

	private static final long serialVersionUID = 1L;
	private IModel<String> classNameModel;
	
	public OClassModel(OClass oClass) {
		super(oClass);
	}

	public OClassModel(String className) {
		this.classNameModel = Model.of(className);
	}
	
	public OClassModel(IModel<String> classNameModel) {
		this.classNameModel = classNameModel;
	}

	@Override
	protected OClass loadInstance() {
		String className = classNameModel!=null?classNameModel.getObject():null;
		return className!=null?getSchema().getClass(className):null;
	}
	
	
	@Override
	protected void handleObject(OClass object) {
		String name = object!=null?object.getName():null;
		if(classNameModel!=null)
		{
			classNameModel.setObject(name);
		}
		else
		{
			classNameModel = Model.of(name);
		}
	}
	
	@Override
	protected void onDetach() {
		if(classNameModel!=null)
		{
			OClass thisClass = getObject();
			if(thisClass!=null && !thisClass.getName().equals(classNameModel.getObject()))
			{
				classNameModel.setObject(thisClass.getName());
			}
			classNameModel.detach();
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
	public Class<OClass> getObjectClass() {
		return OClass.class;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((classNameModel == null || classNameModel.getObject()==null) ? 0 : classNameModel.getObject().hashCode());
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
		OClassModel other = (OClassModel) obj;
		if (classNameModel == null) {
			if (other.classNameModel != null)
				return false;
		} else 
			return Objects.equals(classNameModel.getObject(), other.classNameModel.getObject());
		return true;
	}
	
	

}
