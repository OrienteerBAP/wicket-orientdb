package ru.ydn.wicket.wicketorientdb.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.db.record.ORecordOperation;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.tx.OTransaction;

/**
 * {@link Behavior} to disable component if {@link OIdentifiable} was not saved in DB
 */
public class DisableIfDocumentNotSavedBehavior extends Behavior {
	
	private static final long serialVersionUID = 1L;
	public static final DisableIfDocumentNotSavedBehavior INSTANCE = new DisableIfDocumentNotSavedBehavior();
	
	@Override
	public void onConfigure(Component component) {
		super.onConfigure(component);
		Object object = component.getDefaultModelObject();
		if(object!=null && object instanceof OIdentifiable) {
			ORID rid = ((OIdentifiable)object).getIdentity();
			if(rid.isPersistent()) {
				component.setEnabled(true);
			} else {
				// Is record scheduled for creation?
				OTransaction transaction = OrientDbWebSession.get().getDatabase().getTransaction();
				ORecordOperation operation = transaction.getRecordEntry(rid);
				component.setEnabled(operation!=null && operation.type==ORecordOperation.CREATED);
			}
		}
	}
}
