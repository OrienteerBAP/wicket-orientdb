package ru.ydn.wicket.wicketorientdb.proto;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.core.util.lang.PropertyResolverConverter;

import com.google.common.primitives.Primitives;
import com.google.common.reflect.AbstractInvocationHandler;

/**
 * Base class for creation of Prototypers. Creation of new {@link IPrototype} should look like MyPrototyper.newPrototype(...)
 * 
 * @param <T>
 */
public abstract class AbstractPrototyper<T> extends AbstractInvocationHandler implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Enum of possible types of operations over an object
	 */
	public static enum Operation
	{
		GET("get", new Class<?>[0], null, -1),
		IS("is", new Class<?>[0], null, -1),
		SET("set", new Class<?>[]{Object.class}, null, 0),
		GET_MAPPED("get", new Class<?>[]{String.class}, ":{0}", -1),
		SET_MAPPED("set", new Class<?>[]{String.class, Object.class}, ":{0}", 1);
		
		private final String prefix;
		private final Class<?>[] requiredAttrTypes;
		private final String suffix;
		private final int valueArgIndex;
		private Operation(String prefix, Class<?>[] requiredAttrTypes, String suffix, int valueArgIndex)
		{
			this.prefix = prefix;
			this.requiredAttrTypes = requiredAttrTypes;
			this.suffix = suffix;
			this.valueArgIndex = valueArgIndex;
		}
		public boolean isGeeter() {
			return valueArgIndex<0;
		}
		
		public boolean isSeeter() {
			return valueArgIndex>=0;
		}
		
		/**
		 * Get wicket compatible property expression
		 * @param method {@link Method}
		 * @param args arguments for an invocation
		 * @return wicket compatible property expression
		 */
		public String toPropertyName(Method method, Object[] args)
		{
			String methodName = method.getName();
			if(methodName.startsWith(prefix) && args.length==requiredAttrTypes.length)
			{
				for(int i=0; i<args.length; i++)
				{
					if(args!=null && !(requiredAttrTypes[i].isInstance(args[i]) || args[i]==null)) return null;
				}
				int pLength = prefix.length();
				String simplePropertyName = methodName.substring(pLength, pLength+1).toLowerCase()+methodName.substring(pLength+1);
				if(suffix==null) return simplePropertyName;
				return MessageFormat.format(simplePropertyName+suffix, args);
			}
			else
			{
				return null;
			}
		}
		
	}
	
	protected Map<String, Object> values = new HashMap<String, Object>();
	
	protected T thisProxy;
	
	protected IPrototypeListener<T> listener;
	
	private transient T realized;
	
	AbstractPrototyper() {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Object handleInvocation(Object proxy, Method method, Object[] args)
			throws Throwable {
		String methodName = method.getName();
		if(methodName.equals("isPrototypeRealized"))
		{
			return realized!=null;
		}
		else if(methodName.equals("realizePrototype"))
		{
			return handleRealize((T)proxy);
		}
		else if(methodName.equals("obtainRealizedObject"))
		{
			return realized;
		}
		else if(methodName.equals("thisPrototype"))
		{
			return proxy;
		}
		
		if(realized!=null)
		{
			return method.invoke(realized, args);
		}
		else
		{
			for (Operation operation : Operation.values())
			{
				String propertyName = operation.toPropertyName(method, args);
				if(propertyName!=null)
				{
					if(thisProxy!=proxy)
					{
						for (Map.Entry<String, Object> entry : values.entrySet())
						{
							if(entry.getValue()==proxy)
							{
								propertyName=entry.getKey()+"."+propertyName;
								break;
							}
						}
					}
					if(operation.isGeeter())
					{
						return handleGet(propertyName, method.getReturnType());
					}
					else if(operation.isSeeter())
					{
						return handleSet(propertyName, args[operation.valueArgIndex]);
					}
				}
			}
			
			return handleCustom(proxy, method, args);
		}
	}

	/**
	 * Creation of actual instance from {@link IPrototype}
	 * @param proxy
	 * @return realized instance
	 */
	protected T handleRealize(T proxy)
	{
		T ret = createInstance(proxy);
		PropertyResolverConverter prc = getPropertyResolverConverter();
		for (Map.Entry<String, Object> entry: values.entrySet()) {
			String propertyName = entry.getKey();
			Object value = entry.getValue();
			int mappedIndicatorIndx = propertyName.indexOf(':');
			if(mappedIndicatorIndx<0)
			{
				try
				{
					PropertyResolver.setValue(propertyName, ret, value, prc);
				} catch (WicketRuntimeException e)
				{
//					LOG.error("Can't set property "+propertyName, e);
					e.printStackTrace();
					// NOP
				}
			}
			else
			{
				String propertyExp = propertyName.substring(0, mappedIndicatorIndx);
				String fieldExp = propertyName.substring(mappedIndicatorIndx+1);
				Object object = ret;
				int subObject = propertyExp.lastIndexOf('.');
				if(subObject>=0)
				{
					object = PropertyResolver.getValue(propertyExp.substring(0, subObject), object);
					propertyExp = propertyExp.substring(subObject+1);
				}
				if(object!=null)
				{
					String methodName="set"+propertyExp.substring(0, 1).toUpperCase()+propertyExp.substring(1);
					Method[] methods = object.getClass().getMethods();
					try
					{
						for (Method method : methods)
						{
							if(method.getName().equals(methodName))
							{
								Class<?>[] paramTypes = method.getParameterTypes();
								if(paramTypes.length==2 
										&& paramTypes[0].isAssignableFrom(String.class)
										&& (value==null || paramTypes[1].isInstance(value)))
								{
									method.invoke(object, fieldExp, value);
									break;
								}
							}
						}
					} catch (Exception e)
					{
//						LOG.error("Can't set property "+propertyName, e);
//						e.printStackTrace();
						// NOP
					} 
					
				}
			}
		}
		realized = ret;
		if(listener!=null) listener.onRealizePrototype((IPrototype<T>)proxy);
		return ret;
	}
	
	protected PropertyResolverConverter getPropertyResolverConverter()
	{
		return null;
	}
	
	/**
	 * Creation of instance from proxy. In realizaion of this method only creation is required: properties will be copied automatically.
	 * @param proxy
	 * @return instantiated instance
	 */
	protected abstract T createInstance(T proxy);
	
	/**
	 * @return Main interface for the {@link IPrototype}
	 */
	protected abstract Class<T> getMainInterface();
	
	/**
	 * @return additional interfaces for the instance if they are required
	 */
	protected Class<?>[] getAdditionalInterfaces()
	{
		return null;
	}
	
	protected Object handleGet(String propName, Class<?> returnType)
	{
		Object ret = values.get(propName);
		if(ret==null)
		{
			ret = getDefaultValue(propName, returnType);
			if(ret!=null) values.put(propName, ret);
		}
		return ret;
	}
	/**
	 * @param propName
	 * @param returnType
	 * @return default value for particular property
	 */
	protected Object getDefaultValue(String propName, Class<?> returnType)
	{
		Object ret = null;
		if(returnType.isPrimitive())
		{
			if(returnType.equals(boolean.class))
			{
				return false;
			}
			else if(returnType.equals(char.class))
			{
				return '\0';
			}
			else
			{
				try
				{
					Class<?> wrapperClass = Primitives.wrap(returnType);
					return wrapperClass.getMethod("valueOf", String.class).invoke(null, "0");
				} catch (Throwable e)
				{
					throw new WicketRuntimeException("Can't create default value for '"+propName+"' which should have type '"+returnType.getName()+"'");
				} 
			}
		}
		return ret;
	}
	
	protected Object handleSet(String propName, Object value)
	{
		values.put(propName, value);
		return null;
	}
	
	protected Object handleCustom(Object proxy, Method method, Object[] args)
	{
		throw new UnsupportedOperationException("Method '"+method.getName()+"' is not supported by proxy: "+this.getClass().getSimpleName());
	}
	
	protected <M> M prototypeForChild(String property, Class<M> classM, Class<?>... classes)
	{
		ClassLoader loader = getClass().getClassLoader();
		Class<?>[] interfaces = new Class<?>[2+classes.length];
		interfaces[0]=classM;
		interfaces[1]=Serializable.class;
		System.arraycopy(classes, 0, interfaces, 2, classes.length);
		Object ret = Proxy.newProxyInstance(loader, interfaces, this);
		return classM.cast(ret);
	}
	
	protected static <T> T newPrototypeInternal(AbstractPrototyper<T> prototype) {
		return newPrototypeInternal(prototype, null);
	}
	
	protected static <T> T newPrototypeInternal(AbstractPrototyper<T> prototype, IPrototypeListener<T> listener)
	{
		ClassLoader loader = prototype.getClass().getClassLoader();
		Class<T> mainInterface = prototype.getMainInterface();
		Class<?>[] interfaces = new Class<?>[]{mainInterface, IPrototype.class};
		Class<?>[] addon = prototype.getAdditionalInterfaces();
		if(addon!=null && addon.length>0)
		{
			Class<?>[] newInterfaces = new Class<?>[interfaces.length+addon.length];
			System.arraycopy(interfaces, 0, newInterfaces, 0, interfaces.length);
			System.arraycopy(addon, 0, newInterfaces, interfaces.length, addon.length);
			interfaces = newInterfaces;
		}
		Object ret = Proxy.newProxyInstance(loader, interfaces, prototype);
		prototype.thisProxy = mainInterface.cast(ret);
		prototype.listener = listener;
		return prototype.thisProxy;
	}
	
	@Override
	public String toString() {
		return "Prototype for '"+getMainInterface().getName()+"'";
	}

}
