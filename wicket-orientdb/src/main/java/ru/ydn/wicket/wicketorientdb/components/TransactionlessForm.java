package ru.ydn.wicket.wicketorientdb.components;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

/**
 * Form component which stops transaction for models updates
 * @param <T> The model object type
 */
public class TransactionlessForm<T> extends Form<T>
{

	private static final long serialVersionUID = 1L;
	private boolean isTransactionActive;

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
		ODatabaseSession db = OrientDbWebSession.get().getDatabaseSession();
		isTransactionActive = db.getTransaction().isActive();
		if(isTransactionActive) db.commit();
	}

	@Override
	protected void onValidateModelObjects() {
		super.onValidateModelObjects();
		if(isTransactionActive) OrientDbWebSession.get().getDatabaseSession().begin();
	}
	
}
