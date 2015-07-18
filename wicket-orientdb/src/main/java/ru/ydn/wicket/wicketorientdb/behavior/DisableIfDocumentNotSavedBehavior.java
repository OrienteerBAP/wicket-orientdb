package ru.ydn.wicket.wicketorientdb.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link Behavior} to disable component if {@link ODocument} was not saved in DB
 */
public class DisableIfDocumentNotSavedBehavior extends Behavior {
	
	public static final DisableIfDocumentNotSavedBehavior INSTANCE = new DisableIfDocumentNotSavedBehavior();
	
	@Override
	public void onConfigure(Component component) {
		super.onConfigure(component);
		Object object = component.getDefaultModelObject();
		if(object!=null && object instanceof ODocument) {
			component.setEnabled(((ODocument)object).getIdentity().isPersistent());
		}
	}
}
