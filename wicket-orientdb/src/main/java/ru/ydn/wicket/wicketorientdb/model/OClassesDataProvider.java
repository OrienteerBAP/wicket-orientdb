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

public class OClassesDataProvider extends SortableDataProvider<OClass, String>
{

	@Override
	public Iterator<? extends OClass> iterator(long first, long count) {
		Collection<OClass> classes = getSchema().getClasses();
		Iterator<OClass> it;
		final SortParam<String> sortParam = getSort();
		if(sortParam!=null)
		{
			/*Ordering<OClass> ordering = new Ordering<OClass>() {

				@Override
				public int compare(OClass left, OClass right) {
					Object leftParam = PropertyResolver.getValue(sortParam.getProperty(), left);
					Object rightParam = PropertyResolver.getValue(sortParam.getProperty(), right);
					if((leftParam==null || leftParam instanceof Comparable<?>)
							&& (rightParam==null || rightParam instanceof Comparable<?>))
					{
						return ComparisonChain.start().compare((Comparable<?>)leftParam, (Comparable<?>)rightParam).result();
					}
					else return 0;
				}
			};*/
			Ordering<OClass> ordering = Ordering.natural().nullsFirst().onResultOf(new Function<OClass, Comparable<?>>() {

				@Override
				public Comparable<?> apply(OClass input) {
					Object value = PropertyResolver.getValue(sortParam.getProperty(), input);
					return value instanceof Comparable?(Comparable<?>)value:null;
				}
			});
			if(!sortParam.isAscending()) ordering = ordering.reverse();
			it=ordering.sortedCopy(classes).iterator();
		}
		else
		{
			it=classes.iterator();
		}
		Iterators.advance(it, (int)first);
		return Iterators.limit(it, (int)count);
	}

	@Override
	public long size() {
		return getSchema().countClasses();
	}

	@Override
	public IModel<OClass> model(OClass object) {
		return new OClassModel(object);
	}

	@Override
	public void detach() {
		
	}
	
	public OSchema getSchema()
	{
		return getDatabase().getMetadata().getSchema();
	}
	
	public ODatabaseRecord getDatabase()
	{
		return OrientDbWebSession.get().getDatabase();
	}
}
