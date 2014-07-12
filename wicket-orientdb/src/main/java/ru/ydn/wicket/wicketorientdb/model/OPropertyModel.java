package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.proto.IPrototype;

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

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
			OClass oClass = classModel.getObject();
			return oClass!=null && propertyName!=null?oClass.getProperty(propertyName):null;
	}
	

	@Override
	protected void handleObject(OProperty object) {
		classModel = new OClassModel(object.getOwnerClass());
		propertyName = object.getName();
	}
	
	@Override
	protected void onDetach() {
		if(classModel!=null) classModel.detach();
	}


	public OSchema getSchema()
	{
		return getDatabase().getMetadata().getSchema();
	}
	
	public ODatabaseRecord getDatabase()
	{
		return OrientDbWebSession.get().getDatabase();
	}
	
}
