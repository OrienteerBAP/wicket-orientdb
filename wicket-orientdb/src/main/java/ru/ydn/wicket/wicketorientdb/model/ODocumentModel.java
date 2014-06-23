package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IObjectClassAwareModel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODocumentModel extends LoadableDetachableModel<ODocument> implements IObjectClassAwareModel<ODocument>
{
	private ORID orid;
	private ODocument savedDocument;
	
	public ODocumentModel(ODocument iDocument) {
		super(iDocument);
		orid=iDocument.getIdentity();
	}

	public ODocumentModel(ORID iRID) {
		this.orid = iRID;
	}

	@Override
	public Class<ODocument> getObjectClass() {
		return ODocument.class;
	}
	
	

	@Override
	protected ODocument load() {
		if(orid!=null && orid.isValid())
		{
			ODocument ret = new ODocument(orid);
			ret.load();
			return ret;
		}
		else
		{
			return savedDocument;
		}
	}
	
	@Override
    public void detach()
    {
		ODocument doc = getObject();
		if(doc!=null)
		{
	        this.orid = doc.getIdentity();
	        if(orid!=null && orid.isValid())
	        {
	        	savedDocument=null;
	        }
	        else
	        {
	        	orid=null;
	        	savedDocument = doc;
	        }
		}
		super.detach();
    }

}
