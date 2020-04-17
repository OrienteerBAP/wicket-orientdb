package ru.ydn.wicket.wicketorientdb.model;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;

/**
 * Smart {@link ForwardingDataProvider} which provides links for a property of a document.
 * It uses {@link ODocumentLinksQueryDataProvider} if document exists and {@link ODocumentLinksJavaSortableDataProvider} if not
 */
public class ODocumentLinksDataProvider extends ForwardingDataProvider<ODocument, String> {

	private static final long serialVersionUID = 1L;
	private IModel<ODocument> docModel;
	private IModel<OProperty> propertyModel;
	
	private ODocumentLinksJavaSortableDataProvider<String> javaSortableProvider;
	private ODocumentLinksQueryDataProvider queryProvider;
	
	private SortableDataProvider<ODocument, String> thisRunProvider;
	
	public ODocumentLinksDataProvider(IModel<ODocument> docModel, OProperty property) {
		this(docModel, new OPropertyModel(property));
	}
	
	public ODocumentLinksDataProvider(IModel<ODocument> docModel, IModel<OProperty> propertyModel) {
		this.docModel = docModel;
		this.propertyModel = propertyModel;
	}
	
	@Override
	public void detach() {
//		super.detach();
		propertyModel.detach();
		docModel.detach();
		if(thisRunProvider!=null)
		{
			thisRunProvider.detach();
			thisRunProvider = null;
		}
	}

	@Override
	protected SortableDataProvider<ODocument, String> delegate() {
		if(thisRunProvider==null)
		{
			if(useQueryProvider())
			{
				if(queryProvider==null) queryProvider = new ODocumentLinksQueryDataProvider(docModel, propertyModel) {
					private static final long serialVersionUID = 1L;

					@Override
					public SortParam<String> getSort() {
						return ODocumentLinksDataProvider.this.getSort();
					}
				};
				thisRunProvider = queryProvider;
			}
			else
			{
				if(javaSortableProvider==null) javaSortableProvider = new ODocumentLinksJavaSortableDataProvider<String>(docModel, propertyModel){
					private static final long serialVersionUID = 1L;

					@Override
					public SortParam<String> getSort() {
						return ODocumentLinksDataProvider.this.getSort();
					}
				};
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

	@Override
	public boolean isFilterEnable() {
		boolean enable = false;
		if (useQueryProvider()) {
			delegate();
			enable = queryProvider.isFilterEnable();
		}
		return enable;
	}

	@Override
	public OQueryModel<ODocument> getFilterState() {
		OQueryModel<ODocument> model = null;
		if (useQueryProvider()) {
			delegate();
			model = queryProvider.getFilterState();
		}
		return model;
	}
}
