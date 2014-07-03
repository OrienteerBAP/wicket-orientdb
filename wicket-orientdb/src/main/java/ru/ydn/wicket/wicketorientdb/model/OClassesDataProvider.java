package ru.ydn.wicket.wicketorientdb.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

public class OClassesDataProvider extends AbstractJavaSortableDataProvider<OClass, String>
{
	public OClassesDataProvider() {
		super(new LoadableDetachableModel<Collection<OClass>>() {

			@Override
			protected Collection<OClass> load() {
				return getSchema().getClasses();
			}
		});
	}
	
	public OClassesDataProvider(IModel<Collection<OClass>> dataModel) {
		super(dataModel);
	}

	@Override
	public IModel<OClass> model(OClass object) {
		return new OClassModel(object);
	}
	
	protected static OSchema getSchema()
	{
		return OrientDbWebSession.get().getDatabase().getMetadata().getSchema();
	}
}
