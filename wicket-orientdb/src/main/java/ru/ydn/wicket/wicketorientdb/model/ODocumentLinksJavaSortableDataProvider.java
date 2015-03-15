package ru.ydn.wicket.wicketorientdb.model;

import java.util.Collection;

import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODocumentLinksJavaSortableDataProvider<S> extends
		AbstractJavaSortableDataProvider<ODocument, S> {

	public ODocumentLinksJavaSortableDataProvider(IModel<ODocument> docModel, IModel<OProperty> propertyModel) {
		this(new DynamicPropertyValueModel<Collection<ODocument>>(docModel, propertyModel));
	}
	
	public ODocumentLinksJavaSortableDataProvider(
			IModel<? extends Collection<ODocument>> dataModel) {
		super(dataModel);
	}
	
	@Override
	protected Comparable<?> comparableValue(ODocument input, S sortParam)
	{
		String property = getSortPropertyExpression(sortParam);
		if(property==null) return null;
		Object value = input.field(property);
		return value instanceof Comparable?(Comparable<?>)value:null;
	}
	
	@Override
	public IModel<ODocument> model(ODocument object) {
		return new ODocumentModel(object);
	}

}
