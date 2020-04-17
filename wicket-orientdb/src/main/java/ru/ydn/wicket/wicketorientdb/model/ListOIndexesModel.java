package ru.ydn.wicket.wicketorientdb.model;

import java.util.Collection;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * {@link IModel} to list all {@link OIndex}es for a given {@link OClass}
 */
public class ListOIndexesModel extends AbstractListModel<OIndex<?>>
{
	private static final long serialVersionUID = 1L;
	private IModel<OClass> oClassModel;
	private IModel<Boolean> allIndexesModel;
	
	public ListOIndexesModel(final IModel<OClass> oClassModel, final IModel<Boolean> allIndexesModel)
	{
		Args.notNull(oClassModel, "oClassModel");
		this.oClassModel = oClassModel;
		this.allIndexesModel = allIndexesModel;
	}

	@Override
	public Collection<OIndex<?>> getData() {
		OClass oClass = oClassModel.getObject();
		if(oClass==null)
		{
			return null;
		}
		else if(allIndexesModel==null||Boolean.TRUE.equals(allIndexesModel.getObject()))
		{
			return oClass.getIndexes();
		}
		else
		{
			return oClass.getClassIndexes();
		}
	}

	@Override
	public void detach() {
		super.detach();
		if(allIndexesModel!=null) allIndexesModel.detach();
		oClassModel.detach();
	}

}
