package ru.ydn.wicket.wicketorientdb.proto;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.core.util.lang.PropertyResolverConverter;

import com.google.common.primitives.Primitives;
import com.google.common.reflect.AbstractInvocationHandler;

public abstract class AbstractPrototyper<T> extends AbstractInvocationHandler implements Serializable {
	public static enum Operation
	{
		GET("get", true, 0), IS("is", true, 0), SET("set", false, 1);
		
		private final String prefix;
		private final boolean geeter;
		private final int requiredAttrs;
		private Operation(String prefix, boolean getter, int requiredAttrs)
		{
			this.prefix = prefix;
			this.geeter = getter;
			this.requiredAttrs = requiredAttrs;
		}
		public boolean isGeeter() {
			return geeter;
		}
		
		public boolean isSeeter() {
			return !geeter;
		}
		
		public String toPropertyName(Method method, Object[] args)
		{
			String methodName = method.getName();
			if(methodName.startsWith(prefix) && args.length==requiredAttrs)
			{
				int pLength = prefix.length();
				return  methodName.substring(pLength, pLength+1).toLowerCase()+methodName.substring(pLength+1);
			}
			else
			{
				return null;
			}
		}
		
	}
	private static final String GET = "get";
	private static final String IS = "is";
	private static final String SET = "set";
	
	protected Map<String, Object> values = new HashMap<String, Object>();
	
	protected T thisProxy;
	
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
					return handleSet(propertyName, args[0]);
				}
			}
		}
		
		return handleCustom(proxy, method, args);
	}

	
	protected T handleRealize(T proxy)
	{
		T ret = createInstance(proxy);
		for (Map.Entry<String, Object> entry: values.entrySet()) {
			try
			{
				PropertyResolver.setValue(entry.getKey(), ret, entry.getValue(), null);
			} catch (WicketRuntimeException e)
			{
				// NOP
			}
		}
		realized = ret;
		return ret;
	}
	
	protected abstract T createInstance(T proxy);
	
	protected abstract Class<T> getMainInterface();
	
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
	
	static <T> T newPrototype(AbstractPrototyper<T> prototype)
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
		return prototype.thisProxy;
	}
	
	@Override
	public String toString() {
		return "Prototype for '"+getMainInterface().getName()+"'";
	}

}
