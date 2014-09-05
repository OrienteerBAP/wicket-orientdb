package ru.ydn.wicket.wicketorientdb.components;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

public class TransactionlessForm<T> extends Form<T>
{

	public TransactionlessForm(String id, IModel<T> model)
	{
		super(id, model);
	}

	public TransactionlessForm(String id)
	{
		super(id);
	}

	@Override
	protected void beforeUpdateFormComponentModels() {
		super.beforeUpdateFormComponentModels();
		OrientDbWebSession.get().getDatabase().commit();
	}

	@Override
	protected void onValidateModelObjects() {
		OrientDbWebSession.get().getDatabase().begin();
		super.onValidateModelObjects();
	}
	
}
