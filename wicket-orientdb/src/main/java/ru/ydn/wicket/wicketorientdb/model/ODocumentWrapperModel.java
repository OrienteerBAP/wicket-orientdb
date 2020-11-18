package ru.ydn.wicket.wicketorientdb.model;

import com.orientechnologies.orient.core.exception.ORecordNotFoundException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * {@link IModel} for a {@link ODocumentWrapper}s
 *
 * @param <T> {@link ODocumentWrapper} type
 */
public class ODocumentWrapperModel<T extends ODocumentWrapper> extends Model<T> {
	private static final long serialVersionUID = 1L;
	
	private boolean needToReload=false;

	public ODocumentWrapperModel() {
		super();
	}

	public ODocumentWrapperModel(T object) {
		super(object);
		needToReload=false;
	}
	

	@Override
	public T getObject() {
		try {
			T ret = super.getObject();
			if( ret != null && needToReload) {
				ret.getDocument().load();
				needToReload = false;
			}
			return ret;
		} catch (ORecordNotFoundException e) {
			return null;
		}
	}

	@Override
	public void setObject(T object) {
		super.setObject(object);
		needToReload = false;
	}

	@Override
	public void detach() {
		T ret = getObject();
		if (ret != null && !ret.getDocument().getIdentity().isNew()) {
			needToReload = true;
		}
		super.detach();
	}
	
}
