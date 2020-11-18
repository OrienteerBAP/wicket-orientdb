package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.Component;
import org.apache.wicket.model.IComponentInheritedModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IObjectClassAwareModel;
import org.apache.wicket.model.IWrapModel;
import org.apache.wicket.model.LoadableDetachableModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.exception.ORecordNotFoundException;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Model for storing of {@link ODocument}
 */
public class ODocumentModel extends LoadableDetachableModel<ODocument> implements IObjectClassAwareModel<ODocument>, IComponentInheritedModel<ODocument>
{
	private static final long serialVersionUID = 1L;
	private ORID orid;
	private ODocument savedDocument;
	
	private boolean autoSave=false;
	
	public ODocumentModel() {
		this((ODocument)null);
	}
	
	public ODocumentModel(OIdentifiable identifiable) {
		super(identifiable!=null?(ODocument)identifiable.getRecord():null);
		if(identifiable!=null) this.orid = identifiable.getIdentity();
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
	
	@Override
	protected ODocument load() {
		if(orid!=null && orid.isValid())
		{
			try {
				return orid.getRecord();
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
		        if(orid!=null && orid.isValid())
		        {
		        	savedDocument=null;
		        }
		        else
		        {
		        	orid=null;
		        	savedDocument = doc;
		        	//Should be there to avoid double documents and rolling back to prev doc
		        	//Related issue https://github.com/orientechnologies/orientdb/issues/7646
		        	//TODO: remove when that will be fixed in OrientDB
		        	ORecordInternal.setDirtyManager(savedDocument, null);
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (autoSave ? 1231 : 1237);
		result = prime * result + ((orid == null) ? 0 : orid.hashCode());
		result = prime * result
				+ ((savedDocument == null) ? 0 : savedDocument.hashCode());
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
		ODocumentModel other = (ODocumentModel) obj;
		if (autoSave != other.autoSave)
			return false;
		if (orid == null) {
			if (other.orid != null)
				return false;
		} else if (!orid.equals(other.orid))
			return false;
		if (savedDocument == null) {
			if (other.savedDocument != null)
				return false;
		} else if (!savedDocument.equals(other.savedDocument))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ODocumentModel [orid=" + orid + "]";
	}

	@Override
	public <W> IWrapModel<W> wrapOnInheritance(final Component component) {
		return new IWrapModel<W>() {

			private static final long serialVersionUID = 1L;

			@Override
			public W getObject() {
				ODocument doc = ODocumentModel.this.getObject();
				return doc!=null?(W)doc.field(component.getId()):null;
			}

			@Override
			public void setObject(W object) {
				ODocument doc = ODocumentModel.this.getObject();
				if(doc!=null) doc.field(component.getId(), object);
			}

			@Override
			public void detach() {
				ODocumentModel.this.detach();
			}

			@Override
			public IModel<?> getWrappedModel() {
				return ODocumentModel.this;
			}
		};
	}

}
