package ru.ydn.wicket.wicketorientdb.components;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

/**
 * Utility {@link Form} for dialogs
 *
 * @param <T> The model object type
 */
public class RootForm<T>  extends Form<T>{

	public RootForm(String id, IModel<T> model) {
		super(id, model);
	}

	public RootForm(String id) {
		super(id);
	}

	@Override
	public Form<?> getRootForm() {
		return this;
	}
}
