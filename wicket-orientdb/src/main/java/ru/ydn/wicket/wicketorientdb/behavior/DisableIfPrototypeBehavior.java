package ru.ydn.wicket.wicketorientdb.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

import ru.ydn.wicket.wicketorientdb.proto.IPrototype;

/**
 * Behaviour to disable interface of model object is {@link IPrototype}
 */
public class DisableIfPrototypeBehavior extends Behavior {
	
	private static final long serialVersionUID = 1L;
	public static final DisableIfPrototypeBehavior INSTANCE = new DisableIfPrototypeBehavior();
	
	@Override
	public void onConfigure(Component component) {
		Object object = component.getDefaultModelObject();
		component.setEnabled(!(object instanceof IPrototype));
	}

}
