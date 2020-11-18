package ru.ydn.wicket.wicketorientdb.model;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.index.OIndexManagerAbstract;
import org.apache.wicket.model.IModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.index.OIndexDefinition;
import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * Model for storing {@link OIndex}
 */
public class OIndexModel extends PrototypeLoadableDetachableModel<OIndex>
{
	private static final long serialVersionUID = 1L;
	private IModel<OClass> classModel;
	private String indexName;
	public OIndexModel(OIndex object)
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
	protected OIndex loadInstance() {
		OClass oClass = classModel!=null?classModel.getObject():null;
		ODatabaseDocumentInternal database = OrientDbWebSession.get().getDatabaseDocumentInternal();
		OIndexManagerAbstract indexManager = database.getMetadata().getIndexManagerInternal();
		return oClass != null ? indexManager.getClassIndex(database, oClass.getName(), indexName) : indexManager.getIndex(database, indexName);
	}

	@Override
	protected void handleObject(OIndex object) {
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class<OIndex> getObjectClass() {
		return (Class)OIndex.class;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((classModel == null) ? 0 : classModel.hashCode());
		result = prime * result
				+ ((indexName == null) ? 0 : indexName.hashCode());
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
		OIndexModel other = (OIndexModel) obj;
		if (classModel == null) {
			if (other.classModel != null)
				return false;
		} else if (!classModel.equals(other.classModel))
			return false;
		if (indexName == null) {
			if (other.indexName != null)
				return false;
		} else if (!indexName.equals(other.indexName))
			return false;
		return true;
	}
	
}