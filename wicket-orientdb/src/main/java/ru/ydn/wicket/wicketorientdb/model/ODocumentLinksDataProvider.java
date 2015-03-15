package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODocumentLinksDataProvider extends ForwardingDataProvider<ODocument, String> {

	private IModel<ODocument> docModel;
	private IModel<OProperty> propertyModel;
	
	private ODocumentLinksJavaSortableDataProvider<String> javaSortableProvider;
	private ODocumentLinksQueryDataProvider queryProvider;
	
	private SortableDataProvider<ODocument, String> thisRunProvider;
	
	public ODocumentLinksDataProvider(IModel<ODocument> docModel, IModel<OProperty> propertyModel) {
		this.docModel = docModel;
		this.propertyModel = propertyModel;
	}
	
	@Override
	public void detach() {
		super.detach();
		propertyModel.detach();
		docModel.detach();
		thisRunProvider = null;
	}
	
	@Override
	protected SortableDataProvider delegate() {
		if(thisRunProvider==null)
		{
			if(useQueryProvider())
			{
				if(queryProvider==null) queryProvider = new ODocumentLinksQueryDataProvider(docModel, propertyModel);
				thisRunProvider = queryProvider;
			}
			else
			{
				if(javaSortableProvider==null) javaSortableProvider = new ODocumentLinksJavaSortableDataProvider<String>(docModel, propertyModel);
				thisRunProvider = javaSortableProvider;
			}
		}
		return thisRunProvider;
	}
	
	protected boolean useQueryProvider()
	{
		ODocument doc = docModel.getObject();
		return doc.getIdentity().isPersistent();
	}

	@Override
	public IModel<ODocument> model(ODocument object) {
		return new ODocumentModel(object);
	}
}
