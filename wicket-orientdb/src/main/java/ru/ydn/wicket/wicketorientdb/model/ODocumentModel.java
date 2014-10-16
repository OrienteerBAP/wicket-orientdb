package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IObjectClassAwareModel;
import org.apache.wicket.model.LoadableDetachableModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.exception.ORecordNotFoundException;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Model for storing of {@link ODocument}
 */
public class ODocumentModel extends LoadableDetachableModel<ODocument> implements IObjectClassAwareModel<ODocument>
{
	private static final long serialVersionUID = 1L;
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
			try {
				ODatabaseRecord db = OrientDbWebSession.get().getDatabase();
				return db.load(orid);
			} catch (ORecordNotFoundException e) {
				return null;
			}
		}
		else
		{
			return savedDocument;
		}
	}
	
	@Override
    public void detach()
    {
		ODocument doc = isAttached()?getObject():null;
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

	@Override
	public String toString() {
		return "ODocumentModel [orid=" + orid + "]";
	}
	
	
	
	

}
