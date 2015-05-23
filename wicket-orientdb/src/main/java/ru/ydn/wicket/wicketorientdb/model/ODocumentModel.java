package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IObjectClassAwareModel;
import org.apache.wicket.model.LoadableDetachableModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
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
	
	private boolean autoSave=false;
	private boolean preserveDraft=false;
	
	public ODocumentModel() {
		this((ODocument)null);
	}
	
	public ODocumentModel(ODocument iDocument) {
		super(iDocument);
		if(iDocument!=null) orid=iDocument.getIdentity();
	}

	public ODocumentModel(ORID iRID) {
		this.orid = iRID;
	}

	@Override
	public Class<ODocument> getObjectClass() {
		return ODocument.class;
	}
	
	/**
	 * Get {@link ORID} of a stored {@link ODocument}
	 * @return identifactor {@link ORID}
	 */
	public ORID getIdentity() {
		if(orid!=null) return orid;
		ODocument doc = getObject();
		return doc!=null?doc.getIdentity():null;
	}
	
	public boolean isAutoSave() {
		return autoSave;
	}
	
	public ODocumentModel setAutoSave(boolean autoSave) {
		this.autoSave = autoSave;
		return this;
	}
	
	public boolean isPreserveDraft() {
		return preserveDraft;
	}
	
	public ODocumentModel setPreserveDraft(boolean preserveDraft) {
		this.preserveDraft = preserveDraft;
		return this;
	}

	@Override
	protected ODocument load() {
		if(orid!=null && orid.isValid())
		{
			try {
				ODatabaseDocument db = OrientDbWebSession.get().getDatabase();
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
		if(isAttached())
		{
			ODocument doc = getObject();
			if(doc!=null)
			{
				if(autoSave) doc.save();
		        this.orid = doc.getIdentity();
		        if(orid!=null && orid.isValid() && (!preserveDraft || !doc.isDirty()))
		        {
		        	savedDocument=null;
		        }
		        else
		        {
		        	orid=null;
		        	savedDocument = doc;
		        }
			}
			else
			{
				orid=null;
				savedDocument=null;
			}
		}
		super.detach();
    }

	@Override
	public String toString() {
		return "ODocumentModel [orid=" + orid + "]";
	}

}
