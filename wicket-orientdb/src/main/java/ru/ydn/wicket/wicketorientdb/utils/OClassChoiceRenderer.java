package ru.ydn.wicket.wicketorientdb.utils;

import java.util.List;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.OClassNamingModel;

import com.orientechnologies.orient.core.metadata.schema.OClass;

/**
 * {@link IChoiceRenderer} for {@link OClass}es
 */
public class OClassChoiceRenderer implements IChoiceRenderer<OClass> {
	
	public static final OClassChoiceRenderer INSTANCE = new OClassChoiceRenderer(true);
	public static final OClassChoiceRenderer INSTANCE_NO_LOCALIZATION = new OClassChoiceRenderer(false);

	private static final long serialVersionUID = 1L;
	
	private boolean localize;
	
	public OClassChoiceRenderer(boolean localize) {
		this.localize = localize;
	}

	@Override
	public Object getDisplayValue(OClass object) {
		return localize?new OClassNamingModel(object).getObject():object.getName();
	}

	@Override
	public String getIdValue(OClass object, int index) {
		return object.getName();
	}

	@Override
	public OClass getObject(String id,
			IModel<? extends List<? extends OClass>> choicesModel) {
		OClass ret = OrientDbWebSession.get().getDatabase().getMetadata().getSchema().getClass(id);
		List<? extends OClass> choices = choicesModel.getObject();
		return choices!=null && choices.contains(ret) ? ret : null;
	}
}
