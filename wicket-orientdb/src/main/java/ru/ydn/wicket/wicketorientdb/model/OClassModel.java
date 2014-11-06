package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

/**
 * Model for storing {@link OClass} instance
 */
public class OClassModel extends PrototypeLoadableDetachableModel<OClass> {

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
		if(classNameModel!=null) classNameModel.detach();
	}

	public OSchema getSchema()
	{
		return getDatabase().getMetadata().getSchema();
	}
	
	public ODatabaseDocument getDatabase()
	{
		return OrientDbWebSession.get().getDatabase();
	}

}
