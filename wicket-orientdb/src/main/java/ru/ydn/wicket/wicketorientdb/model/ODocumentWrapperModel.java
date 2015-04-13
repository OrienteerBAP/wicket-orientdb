package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * {@link IModel} for a {@link ODocumentWrapper}s
 *
 * @param <T> {@link ODocumentWrapper} type
 */
public class ODocumentWrapperModel<T extends ODocumentWrapper> extends Model<T>
{
	private static final long serialVersionUID = 1L;
	
	private boolean needToReload=false;
	public ODocumentWrapperModel()
	{
		super();
	}

	public ODocumentWrapperModel(T object)
	{
		super(object);
		needToReload=false;
	}
	

	@Override
	public T getObject() {
		T ret=  super.getObject();
		if(ret!=null && needToReload)
		{
			ret.load();
			needToReload = false;
		}
		return ret;
	}

	@Override
	public void setObject(T object) {
		super.setObject(object);
		needToReload = false;
	}

	@Override
	public void detach() {
		needToReload = true;
		super.detach();
	}
	
}
