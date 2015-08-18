package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.lang.Args;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link IModel} for dynamic getting a value of a property and document specified by models
 *
 * @param <T> value type
 */
public class DynamicPropertyValueModel<T> extends LoadableDetachableModel<T>
{
	private static final long serialVersionUID = 1L;
	protected final IModel<ODocument> docModel;
	protected final IModel<OProperty> propertyModel;
	
	public DynamicPropertyValueModel(IModel<ODocument> docModel, IModel<OProperty> propertyModel)
	{
		Args.notNull(docModel, "documentModel");
		this.docModel = docModel;
		this.propertyModel = propertyModel;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected T load() {
		ODocument doc = docModel.getObject();
		OProperty prop = propertyModel!=null?propertyModel.getObject():null;
		if(doc==null) return null;
		if(prop==null) return (T) doc;
		return (T) doc.field(prop.getName());
	}

	@Override
	protected void onDetach() {
		docModel.detach();
		if(propertyModel!=null) propertyModel.detach();
	}

	@Override
	public void setObject(T object) {
		ODocument doc = docModel.getObject();
		OProperty prop = propertyModel!=null?propertyModel.getObject():null;
		if(doc==null) return;
		if(prop==null)
		{
			if(object instanceof OIdentifiable) docModel.setObject((ODocument)object);
		}
		else
		{
			doc.field(prop.getName(), object);
		}
		super.setObject(object);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((docModel == null) ? 0 : docModel.hashCode());
		result = prime * result
				+ ((propertyModel == null) ? 0 : propertyModel.hashCode());
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
		DynamicPropertyValueModel other = (DynamicPropertyValueModel) obj;
		if (docModel == null) {
			if (other.docModel != null)
				return false;
		} else if (!docModel.equals(other.docModel))
			return false;
		if (propertyModel == null) {
			if (other.propertyModel != null)
				return false;
		} else if (!propertyModel.equals(other.propertyModel))
			return false;
		return true;
	}
	
}
