package ru.ydn.wicket.wicketorientdb.components.table;

import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import ru.ydn.wicket.wicketorientdb.model.ODocumentPropertyModel;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class DocumentPropertyColumn extends PropertyColumn<ODocument, String>
{

	public DocumentPropertyColumn(IModel<String> displayModel,
			String sortProperty, String propertyExpression)
	{
		super(displayModel, sortProperty, propertyExpression);
	}

	public DocumentPropertyColumn(IModel<String> displayModel,
			String propertyExpression)
	{
		super(displayModel, propertyExpression);
	}
	
	@Override
	public IModel<Object> getDataModel(IModel<ODocument> rowModel)
	{
		ODocumentPropertyModel<Object> propertyModel = new ODocumentPropertyModel<Object>(rowModel, getPropertyExpression());
		return propertyModel;
	}

}
