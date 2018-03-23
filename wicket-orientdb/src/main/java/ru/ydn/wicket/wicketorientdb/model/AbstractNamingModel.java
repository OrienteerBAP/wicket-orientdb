package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.model.IComponentAssignedModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.string.Strings;

/**
 * {@link IModel}&lt;{@link String}&gt; for obtaining name for particular objects. 
 * Model calculate resource key for passed object and then return either key value from resources or buitified version of resource key.
 * @param <T> type to be named
 */
public abstract class AbstractNamingModel<T> implements IComponentAssignedModel<String>
{
	private static final long serialVersionUID = 1L;
	private String resourceKey;
	private IModel<T> objectModel;
	
	public AbstractNamingModel(T object)
	{
		this.resourceKey = getResourceKey(object);
	}
	
	public AbstractNamingModel(IModel<T> objectModel)
	{
		this(objectModel, true);
	}
	
	public AbstractNamingModel(IModel<T> objectModel, boolean dynamic)
	{
		if(dynamic)
		{
			this.objectModel = objectModel;
		}
		else
		{
			this.resourceKey = getResourceKey(objectModel.getObject());
		}
	}

	@Override
	public IWrapModel<String> wrapOnAssignment(Component component) {
		return new AssignmentWrapper(component);
	}


	@Override
	public String getObject()
	{
		return getObject(null);
	}
	
	/**
	 * Returns name
	 * @param component component to look associated localization 
	 * @return name
	 */
	public String getObject(Component component) {
		if(objectModel!=null)
		{
			T object = objectModel.getObject();
			if(object==null) return null;
			resourceKey = getResourceKey(object);
		}
		String defaultValue = getDefault();
		if(defaultValue==null) defaultValue = Strings.lastPathComponent(resourceKey, '.');
		if(defaultValue!=null) defaultValue = buitify(defaultValue);
		return Application.get()
				.getResourceSettings()
				.getLocalizer()
				.getString(resourceKey, null, defaultValue);
	}
	
	public abstract String getResourceKey(T object);
	
	public String getDefault()
	{
		return null;
	}
	
	@Override
	public void detach() {
		if(objectModel!=null) objectModel.detach();
	}

	/**
	 * Utility method to make source string more human readable
	 * @param string source string
	 * @return buitified source string
	 */
	public static String buitify(String string)
	{
		char[] chars = string.toCharArray();
		StringBuilder sb = new StringBuilder();
		int lastApplied=0;
		for(int i=0; i<chars.length;i++)
		{
			char pCh = i>0?chars[i-1]:0;
			char ch = chars[i];
			if(ch=='_' || ch=='-' || Character.isWhitespace(ch))
			{
				sb.append(chars, lastApplied, i-lastApplied);
				lastApplied=i+1;
			}
			else if(i==0 && Character.isLowerCase(ch))
			{
				sb.append(Character.toUpperCase(ch));
				lastApplied=i+1;
			}
			else if(i>0 && Character.isUpperCase(ch) && !(Character.isWhitespace(pCh) || pCh=='_' || pCh=='-')&& !Character.isUpperCase(pCh))
			{
				sb.append(chars, lastApplied, i-lastApplied).append(' ').append(ch);
				lastApplied=i+1;
			}
			else if(i>0 && Character.isLowerCase(ch) && (Character.isWhitespace(pCh) || pCh=='_' || pCh=='-'))
			{
				sb.append(chars, lastApplied, i-lastApplied);
				if(sb.length()>0) sb.append(' ');
				sb.append(Character.toUpperCase(ch));
				lastApplied=i+1;
			}
		}
		sb.append(chars, lastApplied, chars.length-lastApplied);
		return sb.toString();
	}
	
	private class AssignmentWrapper extends LoadableDetachableModel<String>
	implements
		IWrapModel<String>
	{
		private static final long serialVersionUID = 1L;
	
		private final Component component;
	
		/**
		 * Construct.
		 * 
		 * @param component
		 */
		public AssignmentWrapper(Component component)
		{
			this.component = component;
		}
	
		/**
		 * @see org.apache.wicket.model.IWrapModel#getWrappedModel()
		 */
		@Override
		public IModel<String> getWrappedModel()
		{
			return AbstractNamingModel.this;
		}
	
		@Override
		protected String load()
		{
			return AbstractNamingModel.this.getObject(component);
		}
	
		@Override
		protected void onDetach()
		{
			AbstractNamingModel.this.detach();
		}
	}
}
