package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.PropertyModel;

import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * Model for obtaining and setting value of a {@link ODocument} property
 * @param <T> The Model object type
 */
public class ODocumentPropertyModel<T> extends PropertyModel<T> {

	private static final long serialVersionUID = 1L;

	public ODocumentPropertyModel(Object modelObject, String expression) {
		super(wrapDocumentIfRequired(modelObject), expression);
	}
	
	/**
	 * Wrap object to {@link ODocumentMapWrapper} if {@link ODocument} was specified
	 * @param obj object to wrap
	 * @return wrapped object
	 */
	public static Object wrapDocumentIfRequired(Object obj)
	{
		return obj instanceof ODocument
					?new ODocumentMapWrapper((ODocument)obj)
					:obj;
	}
	
	/**
	 * Extract {@link ODocument} from inner model
	 * @return {@link ODocument}
	 */
	public ODocument getDocument()
	{
		Object target = getInnermostModelOrObject();
		if(target instanceof ODocument) return (ODocument)target;
		else if(target instanceof ODocumentWrapper) return ((ODocumentWrapper)target).getDocument();
		else return null;
	}

	@Override
	public T getObject() {
		ODocument doc = getDocument();
		String expression = getPropertyExpression();
		if(doc!=null && (!expression.contains(".") || doc.isAllowChainedAccess()))
		{
			return doc.field(getPropertyExpression());
		}
		else
		{
			return super.getObject();
		}
	}

	@Override
	public void setObject(T object) {
		ODocument doc = getDocument();
		String expression = getPropertyExpression();
		if(doc!=null && (!expression.contains(".") || doc.isAllowChainedAccess()))
		{
			doc.field(getPropertyExpression(), object);
		}
		else
		{
			super.setObject(object);
		}
	}
	
	

	

}
