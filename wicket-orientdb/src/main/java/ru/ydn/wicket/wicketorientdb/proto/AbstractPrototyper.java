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
	private static final String GET = "get";
	private static final String IS = "is";
	private static final String SET = "set";
	
	protected Map<String, Object> values = new HashMap<String, Object>();
	
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
		if(methodName.startsWith(GET) && args.length==0)
		{
			String propName = methodName.substring(3, 4).toLowerCase()+methodName.substring(4);
			return handleGet(propName, method.getReturnType());
		}
		else if(methodName.startsWith(IS) && args.length==0)
		{
			String propName = methodName.substring(2, 3).toLowerCase()+methodName.substring(3);
			return handleGet(propName, method.getReturnType());
		}
		else if(methodName.startsWith(SET) &&  args.length==1)
		{
			String propName = methodName.substring(3, 4).toLowerCase()+methodName.substring(4);
			handleSet(propName, args[0]);
			return null;
		}
		else
		{
			return handleCustom(proxy, method, args);
		}
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
	
	protected void handleSet(String propName, Object value)
	{
		values.put(propName, value);
	}
	
	protected Object handleCustom(Object proxy, Method method, Object[] args)
	{
		throw new UnsupportedOperationException("Method '"+method.getName()+"' is not supported by proxy: "+this.getClass().getSimpleName());
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
		return mainInterface.cast(ret);
	}
	
	@Override
	public String toString() {
		return "Prototype for '"+getMainInterface().getName()+"'";
	}

}
