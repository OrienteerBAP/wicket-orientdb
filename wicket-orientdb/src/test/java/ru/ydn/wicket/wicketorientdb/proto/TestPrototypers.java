package ru.ydn.wicket.wicketorientdb.proto;

import java.io.Serializable;
import java.net.NetPermission;
import java.util.Arrays;
import java.util.Collection;

import org.apache.wicket.core.util.lang.PropertyResolver;
import org.junit.ClassRule;
import org.junit.Test;

import com.orientechnologies.orient.core.index.ODefaultIndexFactory;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.ORoundRobinClusterSelectionStrategy;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.junit.WicketOrientDbTesterScope;
import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class TestPrototypers
{
	@ClassRule
	public static WicketOrientDbTesterScope wicket = new WicketOrientDbTesterScope("admin", "admin");
	
	@Test
	public void testMyBeanPrototyper() throws Exception
	{
		IMyBean bean = MyBeanPrototyper.newPrototype(new IPrototypeListener<IMyBean>() {
			
			@Override
			public void onRealizePrototype(IPrototype<IMyBean> prototype) {
				prototype.obtainRealizedObject().setHandledByListener(true); 
			}
		});
		assertTrue(bean instanceof IPrototype<?>);
		assertTrue(bean instanceof Serializable);
		bean.setName("name");
		bean.setDescription(null);
		bean.setInteger(-1);
		assertEquals("name", bean.getName());
		assertNull(bean.getDescription());
		assertEquals(Integer.valueOf(-1), bean.getInteger());
		assertNull(bean.getSignature());
		IPrototype<IMyBean> proto = (IPrototype<IMyBean>)bean;
		assertTrue(proto instanceof IMyBean);
		assertFalse(proto.isPrototypeRealized());
		bean = proto.realizePrototype();
		assertEquals("name", bean.getName());
		assertNull(bean.getDescription());
		assertEquals(Integer.valueOf(-1), bean.getInteger());
		assertEquals("REAL", bean.getSignature());
		assertTrue(proto.isPrototypeRealized());
		assertEquals("REAL", ((IMyBean)proto).getSignature());
		assertTrue(bean.isHandledByListener());
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
		bean.setCustomString("name3", null);
		assertEquals("myname", bean.getCustom("name"));
		assertEquals(Integer.valueOf(-1), bean.getCustom("int"));
		assertEquals("myname2", bean.getCustomString("name2"));
		assertNull(bean.getCustomString("name3"));
		IPrototype<IMyBean> proto = (IPrototype<IMyBean>)bean;
		assertTrue(proto instanceof IMyBean);
		assertFalse(proto.isPrototypeRealized());
		bean = proto.realizePrototype();
		assertEquals("myname", bean.getCustom("name"));
		assertEquals(Integer.valueOf(-1), bean.getCustom("int"));
		assertEquals("myname2", bean.getCustomString("name2"));
		assertNull(bean.getCustomString("name3"));
		assertEquals("REAL", bean.getSignature());
		assertTrue(proto.isPrototypeRealized());
		assertEquals("REAL", ((IMyBean)proto).getSignature());
	}
	
	
	
	@Test
	public void testOClassPrototyper() throws Exception
	{
		OClass newClass = OClassPrototyper.newPrototype();
		assertNull(wicket.getTester().getSchema().getClass("NewClass"));
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
		assertFalse(newClass.hasSuperClasses());
		OClass oRoleClass = wicket.getTester().getSchema().getClass("ORole");
		newClass.addSuperClass(oRoleClass);
		assertTrue(newClass.hasSuperClasses());
		assertTrue(newClass.getSuperClassesNames().contains("ORole"));
		assertTrue(newClass.getSuperClasses().contains(oRoleClass));
		assertTrue(newClass.isSubClassOf(oRoleClass));
		newClass.removeSuperClass(oRoleClass);
		assertFalse(newClass.hasSuperClasses());
		assertFalse(newClass.isSubClassOf(oRoleClass));
		//Realization
		assertTrue(newClass instanceof IPrototype); 
		OClass realizedNewClass = ((IPrototype<OClass>)newClass).realizePrototype();
		assertEquals(wicket.getTester().getSchema().getClass("NewClass"), realizedNewClass);
		assertFalse(realizedNewClass instanceof IPrototype);
		assertEquals("NewClass", realizedNewClass.getName());
		assertEquals(ORoundRobinClusterSelectionStrategy.NAME, realizedNewClass.getClusterSelection().getName());
		wicket.getTester().getSchema().dropClass(realizedNewClass.getName());
	}
	
	@Test
	public void testOPropertyPrototyper() throws Exception
	{
		OClass newClass = wicket.getTester().getSchema().createClass("NewClass");
		OProperty toCompare = newClass.createProperty("toCompare", OType.STRING);
		try
		{
			OProperty newProperty = OPropertyPrototyper.newPrototype("NewClass");
			assertNull(newClass.getProperty("newProperty"));
			newProperty.setName("newProperty");
			assertEquals("newProperty", newProperty.getName());
			assertNull(newClass.getProperty("newProperty"));
			assertEquals("NewClass.newProperty", newProperty.getFullName());
			newProperty.setType(OType.STRING);
			assertEquals(OType.STRING, newProperty.getType());
			newProperty.setCustom("myCustom", "myCustomValue");
			assertEquals("myCustomValue", newProperty.getCustom("myCustom"));
			assertTrue(newProperty.compareTo(toCompare)<0);
			
			//Realization
			assertTrue(newProperty instanceof IPrototype);
			OProperty realizedNewProperty = ((IPrototype<OProperty>)newProperty).realizePrototype();
			assertEquals(newClass.getProperty("newProperty"), realizedNewProperty);
			assertEquals("myCustomValue", realizedNewProperty.getCustom("myCustom"));
		}
		finally
		{
			//Drop
			wicket.getTester().getSchema().dropClass(newClass.getName());
		}
	}
	
	@Test
	public void testOIndexPrototyper() throws Exception
	{
		OClass newClass = wicket.getTester().getSchema().createClass("NewClass");
		OProperty property = newClass.createProperty("name", OType.STRING);
		OIndex newIndex = OIndexPrototyper.newPrototype("NewClass", Arrays.asList("name"));
		assertTrue(property.getAllIndexes().size()==0);
		PropertyResolver.setValue("type", newIndex, "notunique", null);
		assertNotNull(newIndex.getDefinition());
		assertTrue(newIndex.getDefinition().getFields().contains("name"));
		assertTrue(newIndex instanceof IPrototype);
		OIndex realizedNewIndex = ((IPrototype<OIndex>)newIndex).realizePrototype();
		assertEquals(1, property.getAllIndexes().size());
		assertEquals(1, newClass.getIndexes().size());
		
		property = newClass.createProperty("description", OType.STRING);
		newIndex = OIndexPrototyper.newPrototype("NewClass", Arrays.asList("description"));
		PropertyResolver.setValue("type", newIndex, "notunique", null);
		assertEquals(0, property.getAllIndexes().size());
		PropertyResolver.setValue("algorithm", newIndex, "SBTREE", null);
		ODocument metadata = new ODocument();
		metadata.field("test", "test123", OType.STRING);
		PropertyResolver.setValue("metadata", newIndex, metadata, null);
		realizedNewIndex = ((IPrototype<OIndex>)newIndex).realizePrototype();
		assertEquals(1, property.getAllIndexes().size());
		assertEquals(2, newClass.getIndexes().size());
		assertEquals("test123", realizedNewIndex.getMetadata().field("test"));
		
		wicket.getTester().getSchema().dropClass(newClass.getName());
	}
	
}
