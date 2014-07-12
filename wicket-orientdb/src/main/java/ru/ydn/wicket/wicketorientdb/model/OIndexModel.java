package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.index.OIndexDefinition;
import com.orientechnologies.orient.core.index.OIndexManager;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class OIndexModel extends PrototypeLoadableDetachableModel<OIndex<?>>
{
	private IModel<OClass> classModel;
	private String indexName;
	public OIndexModel(OIndex<?> object)
	{
		super(object);
	}
	
	public OIndexModel(String indexName)
	{
		this.indexName = indexName;
	}

	public OIndexModel(OClass oClass, String indexName)
	{
		this.classModel = new OClassModel(oClass);
		this.indexName = indexName;
	}


	@Override
	protected OIndex<?> loadInstance() {
		OClass oClass = classModel!=null?classModel.getObject():null;
		OIndexManager indexManager = OrientDbWebSession.get().getDatabase().getMetadata().getIndexManager();
		return oClass!=null? indexManager.getClassIndex(oClass.getName(), indexName):indexManager.getIndex(indexName);
	}

	@Override
	protected void handleObject(OIndex<?> object) {
		indexName = object.getName();
		OIndexDefinition indexDefinition = object.getDefinition();
		if(indexDefinition!=null)
		{
			String className = indexDefinition.getClassName();
			if(className!=null) classModel = new OClassModel(className);
		}
	}

	@Override
	public void detach() {
		super.detach();
		if(classModel!=null) classModel.detach();
	}
	
	
	
}