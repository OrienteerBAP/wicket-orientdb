package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;

public class OIndexModel extends LoadableDetachableModel<OIndex<?>>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IModel<OClass> classModel;
	private IModel<String> indexNameModel;
	
	public OIndexModel(OIndex<?> oIndex)
	{
		this(new OClassModel(oIndex.getDefinition().getClassName()), oIndex.getName());
	}
	
	public OIndexModel(OClass oClass, OIndex<?> oIndex)
	{
		this(new OClassModel(oClass), oIndex.getName());
	}
	
	public OIndexModel(IModel<OClass> classModel, IModel<String> indexNameModel)
	{
		this.classModel = classModel;
		this.indexNameModel = indexNameModel;
	}
	
	public OIndexModel(String className, String indexName)
	{
		this(new OClassModel(className), indexName);
	}
	
	public OIndexModel(IModel<OClass> classModel, String indexName)
	{
		this.classModel = classModel;
		this.indexNameModel = Model.of(indexName);
	}

	@Override
	protected OIndex<?> load() {
		OClass oClass = classModel.getObject();
		String oIndex = indexNameModel.getObject();
		return oClass!=null && oIndex!=null
				?OrientDbWebSession.get().getDatabase().getMetadata().getIndexManager().getClassIndex(oClass.getName(), oIndex)
				:null;
	}

	@Override
	protected void onDetach() {
		if(classModel!=null) classModel.detach();
		if(indexNameModel!=null) indexNameModel.detach();
	}
	
}