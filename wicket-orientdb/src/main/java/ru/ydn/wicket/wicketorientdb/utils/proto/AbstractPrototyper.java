package ru.ydn.wicket.wicketorientdb.utils.proto;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.core.util.lang.PropertyResolverConverter;

import com.google.common.reflect.AbstractInvocationHandler;

public abstract class AbstractPrototyper<T> extends AbstractInvocationHandler {
	private static final String GET = "get";
	private static final String IS = "is";
	private static final String SET = "set";
	
	protected Map<String, Object> values = new HashMap<String, Object>();
	
	private transient T realized;
	
	AbstractPrototyper() {
	}
	
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
			return handleRealize();
		}
		else if(methodName.equals("obtainRealizedPrototype"))
		{
			return realized;
		}
		if(methodName.startsWith(GET) && args.length==0)
		{
			String propName = methodName.substring(3, 4).toLowerCase()+methodName.substring(4);
			return handleGet(propName);
		}
		else if(methodName.startsWith(IS) && args.length==0)
		{
			String propName = methodName.substring(2, 3).toLowerCase()+methodName.substring(3);
			return handleGet(propName);
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
	
	protected T handleRealize()
	{
		T ret = createInstance();
		PropertyResolverConverter prc = new PropertyResolverConverter(Application.get().getConverterLocator(),
			Session.get().getLocale());
		for (Map.Entry<String, Object> entry: values.entrySet()) {
			PropertyResolver.setValue(entry.getKey(), ret, entry.getValue(), prc);
		}
		realized = ret;
		return ret;
	}
	
	protected abstract T createInstance();
	
	protected abstract Class<T> getMainInterface();
	
	protected Object handleGet(String propName)
	{
		return values.get(propName);
	}
	
	protected void handleSet(String propName, Object value)
	{
		values.put(propName, value);
	}
	
	protected Object handleCustom(Object proxy, Method method, Object[] args)
	{
		throw new UnsupportedOperationException();
	}
	
	static <T> T newPrototype(AbstractPrototyper<T> prototype)
	{
		ClassLoader loader = prototype.getClass().getClassLoader();
		Class<T> mainInterface = prototype.getMainInterface();
		Object ret = Proxy.newProxyInstance(loader, new Class<?>[]{mainInterface, IPrototype.class}, prototype);
		return mainInterface.cast(ret);
	}

}
