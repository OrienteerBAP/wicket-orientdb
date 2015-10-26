package ru.ydn.wicket.wicketorientdb.proto;

import java.util.Locale;

import org.apache.wicket.ConverterLocator;
import org.apache.wicket.core.util.lang.PropertyResolverConverter;

public class MyBeanPrototyper extends AbstractPrototyper<IMyBean>
{

	private static final long serialVersionUID = 1L;

	@Override
	protected IMyBean createInstance(IMyBean proxy) {
		return new MyBean();
	}

	@Override
	protected Class<IMyBean> getMainInterface() {
		return IMyBean.class;
	}
	
	public static IMyBean newPrototype()
	{
		return newPrototype(null);
	}
	
	public static IMyBean newPrototype(IPrototypeListener<IMyBean> listener)
	{
		return newPrototypeInternal(new MyBeanPrototyper(), listener);
	}

	@Override
	protected PropertyResolverConverter getPropertyResolverConverter() {
		return new PropertyResolverConverter(new ConverterLocator(), Locale.getDefault());
	}
	
	

}
