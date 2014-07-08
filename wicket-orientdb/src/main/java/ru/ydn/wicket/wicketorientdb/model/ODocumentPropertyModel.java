package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.PropertyModel;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODocumentPropertyModel<T> extends PropertyModel<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ODocumentPropertyModel(Object modelObject, String expression) {
		super(wrapDocumentIfRequired(modelObject), expression);
	}
	
	public static Object wrapDocumentIfRequired(Object obj)
	{
		return obj instanceof ODocument
					?new ODocumentMapWrapper((ODocument)obj)
					:obj;
	}
	
	public ODocument getDocument()
	{
		Object target = getInnermostModelOrObject();
		if(target instanceof ODocument) return (ODocument)target;
		else if(target instanceof ODocumentMapWrapper) return ((ODocumentMapWrapper)target).getDocument();
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
