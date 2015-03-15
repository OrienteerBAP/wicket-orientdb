package ru.ydn.wicket.wicketorientdb.model;


import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class ODocumentLinksQueryDataProvider extends OQueryDataProvider<ODocument> {
	
	public ODocumentLinksQueryDataProvider(IModel<ODocument> docModel, IModel<OProperty> propertyModel) {
		this(docModel, propertyModel.getObject());
	}
	
	public ODocumentLinksQueryDataProvider(IModel<ODocument> docModel, OProperty property) {
		super("select expand("+property.getName()+") from "+property.getOwnerClass().getName()+" where @rid = :doc");
		setParameter("doc", docModel);
	}
}
