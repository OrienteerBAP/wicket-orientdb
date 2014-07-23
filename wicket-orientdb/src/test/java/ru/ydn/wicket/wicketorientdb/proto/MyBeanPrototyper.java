package ru.ydn.wicket.wicketorientdb.proto;

import java.util.Locale;

import org.apache.wicket.ConverterLocator;
import org.apache.wicket.core.util.lang.PropertyResolverConverter;

public class MyBeanPrototyper extends AbstractPrototyper<IMyBean>
{

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
		return newPrototype(new MyBeanPrototyper());
	}

	@Override
	protected PropertyResolverConverter getPropertyResolverConverter() {
		return new PropertyResolverConverter(new ConverterLocator(), Locale.getDefault());
	}
	
	

}
