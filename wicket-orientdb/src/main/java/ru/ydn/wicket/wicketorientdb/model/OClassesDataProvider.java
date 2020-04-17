package ru.ydn.wicket.wicketorientdb.model;

import java.util.Collection;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.proto.OClassPrototyper;

import com.google.common.base.Predicate;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;

/**
 * {@link SortableDataProvider} for listing of {@link OClass}es
 */
public class OClassesDataProvider extends AbstractJavaSortableDataProvider<OClass, String>
{
	private static final long serialVersionUID = 1L;

	public OClassesDataProvider() {
		super(new ListOClassesModel());
	}
	
	public OClassesDataProvider(final Predicate<OClass> filterPredicate) {
		super(new ListOClassesModel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Predicate<? super OClass> getFilterPredicate() {
				return filterPredicate;
			}
		});
	}
	
	public OClassesDataProvider(IModel<? extends Collection<OClass>> dataModel) {
		super(dataModel);
	}
	

	public OClassesDataProvider(IModel<? extends Collection<OClass>> dataModel, Predicate<OClass> filterPredicate) {
		super(dataModel, filterPredicate);
	}

	@Override
	public IModel<OClass> model(OClass object) {
		return new OClassModel(object);
	}
	
	protected static OSchema getSchema()
	{
		return OrientDbWebSession.get().getDatabase().getMetadata().getSchema();
	}

	@Override
	protected String getSortPropertyExpression(String param) {
		if(OClassPrototyper.SUPER_CLASSES.equals(param)) return "superClass.name"; //Sort just by first super class
		else return super.getSortPropertyExpression(param);
	}
	
	
}
