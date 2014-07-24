package ru.ydn.wicket.wicketorientdb.proto;

import java.io.Serializable;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestMyBeanPrototyper
{
	@Test
	public void testPrototyper() throws Exception
	{
		IMyBean bean = MyBeanPrototyper.newPrototype();
		assertTrue(bean instanceof IPrototype<?>);
		assertTrue(bean instanceof Serializable);
		bean.setName("name");
		bean.setInteger(-1);
		assertEquals("name", bean.getName());
		assertEquals(Integer.valueOf(-1), bean.getInteger());
		assertNull(bean.getSignature());
		IPrototype<IMyBean> proto = (IPrototype<IMyBean>)bean;
		assertTrue(proto instanceof IMyBean);
		assertFalse(proto.isPrototypeRealized());
		bean = proto.realizePrototype();
		assertEquals("name", bean.getName());
		assertEquals(Integer.valueOf(-1), bean.getInteger());
		assertEquals("REAL", bean.getSignature());
		assertTrue(proto.isPrototypeRealized());
		assertEquals("REAL", ((IMyBean)proto).getSignature());
	}
	
	@Test
	public void testMappedPropertiesPrototyper() throws Exception
	{
		IMyBean bean = MyBeanPrototyper.newPrototype();
		assertTrue(bean instanceof IPrototype<?>);
		assertTrue(bean instanceof Serializable);
		bean.setCustom("name", "myname");
		bean.setCustom("int", -1);
		assertEquals("myname", bean.getCustom("name"));
		assertEquals(Integer.valueOf(-1), bean.getCustom("int"));
		IPrototype<IMyBean> proto = (IPrototype<IMyBean>)bean;
		assertTrue(proto instanceof IMyBean);
		assertFalse(proto.isPrototypeRealized());
		bean = proto.realizePrototype();
		assertEquals("myname", bean.getCustom("name"));
		assertEquals(Integer.valueOf(-1), bean.getCustom("int"));
		assertEquals("REAL", bean.getSignature());
		assertTrue(proto.isPrototypeRealized());
		assertEquals("REAL", ((IMyBean)proto).getSignature());
	}
}
