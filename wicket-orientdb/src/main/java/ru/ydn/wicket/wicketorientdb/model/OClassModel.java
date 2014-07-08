package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.utils.proto.IPrototype;

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class OClassModel extends LoadableDetachableModel<OClass> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IModel<ODocument> documentModel;
	private String className;
	private OClass prototype;
	
	public OClassModel(OClass oClass) {
		super(oClass);
		className=oClass.getName();
		if(oClass instanceof IPrototype)
		{
			prototype = oClass;
		}
	}

	public OClassModel(String className) {
		this.className=className;
	}
	
	public OClassModel(IModel<ODocument> documentModel) {
		this.documentModel = documentModel;
	}

	@Override
	protected OClass load() {
		if(documentModel!=null)
		{
			ODocument doc = documentModel.getObject();
			return doc!=null?doc.getSchemaClass():null;
		}
		else
		{
			return prototype!=null?prototype:(className!=null?getSchema().getClass(className):null);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public void detach()
    {
		OClass oClass = getObject();
		if(oClass!=null)
		{
			if(oClass instanceof IPrototype && ((IPrototype<OClass>)oClass).isPrototypeRealized())
			{
				oClass = ((IPrototype<OClass>)oClass).obtainRealizedPrototype();
				prototype = null;
			}
			this.className = oClass.getName();
			super.detach();
		}
		if(documentModel!=null) documentModel.detach();
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
