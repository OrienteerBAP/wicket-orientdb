package ru.ydn.wicket.wicketorientdb.proto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import org.apache.wicket.core.util.lang.PropertyResolver;
import org.junit.Test;

import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.ORoundRobinClusterSelectionStrategy;

import ru.ydn.wicket.wicketorientdb.AbstractTestClass;
import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class TestPrototypers extends AbstractTestClass
{
	@Test
	public void testMyBeanPrototyper() throws Exception
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
		bean.setCustomString("name2", "myname2");
		assertEquals("myname", bean.getCustom("name"));
		assertEquals(Integer.valueOf(-1), bean.getCustom("int"));
		assertEquals("myname2", bean.getCustomString("name2"));
		IPrototype<IMyBean> proto = (IPrototype<IMyBean>)bean;
		assertTrue(proto instanceof IMyBean);
		assertFalse(proto.isPrototypeRealized());
		bean = proto.realizePrototype();
		assertEquals("myname", bean.getCustom("name"));
		assertEquals(Integer.valueOf(-1), bean.getCustom("int"));
		assertEquals("myname2", bean.getCustomString("name2"));
		assertEquals("REAL", bean.getSignature());
		assertTrue(proto.isPrototypeRealized());
		assertEquals("REAL", ((IMyBean)proto).getSignature());
	}
	
	
	
	@Test
	public void testOClassPrototyper() throws Exception
	{
		OClass newClass = OClassPrototyper.newPrototype();
		assertNull(getSchema().getClass("NewClass"));
		newClass.setName("NewClass");
		assertEquals("NewClass", newClass.getName());
		newClass.setClusterSelection(ORoundRobinClusterSelectionStrategy.NAME);
		assertEquals(ORoundRobinClusterSelectionStrategy.NAME, newClass.getClusterSelection().getName());
		Collection<OProperty> properties = newClass.properties();
		assertNotNull(properties);
		assertTrue(properties.size()==0);
		properties = newClass.declaredProperties();
		assertNotNull(properties);
		assertTrue(properties.size()==0);
		//Realization
		assertTrue(newClass instanceof IPrototype); 
		OClass realizedNewClass = ((IPrototype<OClass>)newClass).realizePrototype();
		assertEquals(getSchema().getClass("NewClass"), realizedNewClass);
		assertFalse(realizedNewClass instanceof IPrototype);
		assertEquals("NewClass", realizedNewClass.getName());
		assertEquals(ORoundRobinClusterSelectionStrategy.NAME, realizedNewClass.getClusterSelection().getName());
		getSchema().dropClass(realizedNewClass.getName());
	}
	
	@Test
	public void testOPropertyPrototyper() throws Exception
	{
		OClass newClass = getSchema().createClass("NewClass");
		try
		{
			OProperty newProperty = OPropertyPrototyper.newPrototype("NewClass");
			assertNull(newClass.getProperty("newProperty"));
			newProperty.setName("newProperty");
			assertEquals("newProperty", newProperty.getName());
			assertNull(newClass.getProperty("newProperty"));
			newProperty.setType(OType.STRING);
			assertEquals(OType.STRING, newProperty.getType());
			newProperty.setCustom("myCustom", "myCustomValue");
			assertEquals("myCustomValue", newProperty.getCustom("myCustom"));
			//Realization
			assertTrue(newProperty instanceof IPrototype);
			OProperty realizedNewProperty = ((IPrototype<OProperty>)newProperty).realizePrototype();
			assertEquals(newClass.getProperty("newProperty"), realizedNewProperty);
			assertEquals("myCustomValue", realizedNewProperty.getCustom("myCustom"));
		}
		finally
		{
			//Drop
			getSchema().dropClass(newClass.getName());
		}
	}
	
	@Test
	public void testOIndexPrototyper() throws Exception
	{
		OClass newClass = getSchema().createClass("NewClass");
		OProperty property = newClass.createProperty("name", OType.STRING);
		OIndex<?> newIndex = OIndexPrototyper.newPrototype("NewClass", Arrays.asList("name"));
		assertTrue(property.getAllIndexes().size()==0);
		PropertyResolver.setValue("type", newIndex, "notunique", null);
		assertNotNull(newIndex.getDefinition());
		assertTrue(newIndex.getDefinition().getFields().contains("name"));
		assertTrue(newIndex instanceof IPrototype);
		OIndex<?> realizedNewIndes = ((IPrototype<OIndex<?>>)newIndex).realizePrototype();
		assertTrue(property.getAllIndexes().size()==1);
		
		getSchema().dropClass(newClass.getName());
	}
	
}
