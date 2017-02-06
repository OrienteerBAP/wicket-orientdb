package ru.ydn.wicket.wicketorientdb.utils;

import org.apache.wicket.Application;
import org.apache.wicket.markup.html.form.ChoiceRenderer;

/**
 * {@link ChoiceRenderer} for resource specified lists
 * @param <T> type  of resource
 */
public class ResourceChoiceRenderer<T> extends ChoiceRenderer<T> {
	
	private final String resourcePrefix;

	public ResourceChoiceRenderer(String resourcePrefix) {
		super();
		this.resourcePrefix=resourcePrefix;
	}

	public ResourceChoiceRenderer(String resourcePrefix, String displayExpression, String idExpression) {
		super(displayExpression, idExpression);
		this.resourcePrefix=resourcePrefix;
	}

	public ResourceChoiceRenderer(String resourcePrefix, String displayExpression) {
		super(displayExpression);
		this.resourcePrefix=resourcePrefix;
	}
	
	@Override
	public Object getDisplayValue(T object) {
		String key = resourcePrefix+"."+(object==null?"null":super.getDisplayValue(object));
		return Application.get().getResourceSettings().getLocalizer().getString(key, null);
	}
	
}
