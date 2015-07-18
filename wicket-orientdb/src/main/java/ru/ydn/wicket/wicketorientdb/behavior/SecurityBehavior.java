package ru.ydn.wicket.wicketorientdb.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;

import ru.ydn.wicket.wicketorientdb.security.OSecurityHelper;
import ru.ydn.wicket.wicketorientdb.security.OrientPermission;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * Behavior which can dynamically show/hide or enable/disable a component according to security rights
 * Can use predefined document or a document obtained from a component's model
 */
public class SecurityBehavior extends Behavior
{
	private static final long serialVersionUID = 1L;
	private IModel<ODocument> documentModel;
	private Action action;
	private OrientPermission[] permissions;
	
	private Boolean cachedVisibility;
	
	
	public SecurityBehavior(OrientPermission... permissions)
	{
		this(Component.RENDER, permissions);
	}
	
	public SecurityBehavior(Action action, OrientPermission... permissions)
	{
		this(null, action, permissions);
	}

	public SecurityBehavior(IModel<ODocument> documentModel, OrientPermission... permissions)
	{
		this(documentModel, Component.RENDER, permissions);
	}

	public SecurityBehavior(IModel<ODocument> documentModel, Action action, OrientPermission... permissions)
	{
		this.documentModel = documentModel;
		this.action = action;
		this.permissions = permissions;
	}
	

	@Override
	public void onConfigure(Component component) {
		super.onConfigure(component);
		if(!component.determineVisibility()) return;
		if(documentModel!=null)
		{
			ODocument doc = documentModel.getObject();
			if(cachedVisibility==null)
			{
				cachedVisibility = OSecurityHelper.isAllowed(doc, permissions);
			}
			trigger(component, cachedVisibility);
		}
		else
		{
			Object modelObject = component.getDefaultModelObject();
			if(modelObject instanceof OIdentifiable)
			{
				ODocument doc = ((OIdentifiable)modelObject).getRecord();
				trigger(component, OSecurityHelper.isAllowed(doc, permissions));
			}
		}
	}
	
	protected void trigger(Component component, boolean value)
	{
		if(Component.ENABLE.equals(action)) component.setEnabled(value);
		else if(Component.RENDER.equals(action)) component.setVisibilityAllowed(value);
	}

	@Override
	public void detach(Component component) {
		super.detach(component);
		if(documentModel!=null)
		{
			cachedVisibility = null;
			documentModel.detach();
		}
	}
}
