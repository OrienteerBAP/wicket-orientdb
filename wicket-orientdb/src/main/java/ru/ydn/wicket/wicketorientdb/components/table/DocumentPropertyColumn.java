package ru.ydn.wicket.wicketorientdb.components.table;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Simple implementation of {@link IColumn} for printing field value of a {@link ODocument}
 */
public class DocumentPropertyColumn extends PropertyColumn<ODocument, String>
{

	private static final long serialVersionUID = 1L;

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
		return new PropertyModel<Object>(rowModel, getPropertyExpression());
	}

}
