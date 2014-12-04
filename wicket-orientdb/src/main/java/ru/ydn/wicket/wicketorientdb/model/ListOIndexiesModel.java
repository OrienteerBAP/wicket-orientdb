package ru.ydn.wicket.wicketorientdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class ListOIndexiesModel extends AbstractListModel<OIndex<?>>
{
	private IModel<OClass> oClassModel;
	private IModel<Boolean> allIndexiesModel;
	
	public ListOIndexiesModel(final IModel<OClass> oClassModel, final IModel<Boolean> allIndexiesModel)
	{
		Args.notNull(oClassModel, "oClassModel");
		this.oClassModel = oClassModel;
		this.allIndexiesModel = allIndexiesModel;
	}

	@Override
	public Collection<OIndex<?>> getData() {
		OClass oClass = oClassModel.getObject();
		if(oClass==null)
		{
			return null;
		}
		else if(allIndexiesModel==null||Boolean.TRUE.equals(allIndexiesModel.getObject()))
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
		if(allIndexiesModel!=null) allIndexiesModel.detach();
		oClassModel.detach();
	}

}
