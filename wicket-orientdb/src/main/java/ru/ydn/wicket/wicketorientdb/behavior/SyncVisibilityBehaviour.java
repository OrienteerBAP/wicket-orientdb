package ru.ydn.wicket.wicketorientdb.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;

/**
 * Behavior helps keep visibility in sync with visibility of a provided source component.
 */
public class SyncVisibilityBehaviour extends Behavior
{
	private static final long serialVersionUID = 1L;
	private Component sourceComponent;

	public SyncVisibilityBehaviour(Component sourceComponent)
	{
		this.sourceComponent = sourceComponent;
	}

	@Override
	public void onConfigure(Component component) {
		super.onConfigure(component);
		sourceComponent.configure();
		component.setVisible(sourceComponent.determineVisibility());
	}
	
	

	
}
