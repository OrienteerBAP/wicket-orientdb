package ru.ydn.wicket.wicketorientdb.utils.proto;

import java.lang.reflect.Method;
import java.util.Collections;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OSchemaShared;
import com.orientechnologies.orient.core.metadata.schema.clusterselection.OClusterSelectionStrategy;

public class OClassPrototyper extends AbstractPrototyper<OClass> {
	
	public static interface OClassSetNameFix
	{
		public OClass setName(String iName);
	}
	
	private static final Class<?>[] FIX_INTERFACES = new Class<?>[]{OClassSetNameFix.class}; 
	

	public OClassPrototyper()
	{
		values.put("overSize", (float)0);
	}

	@Override
	protected OClass createInstance() {
		OSchema schema = OrientDbWebSession.get().getDatabase().getMetadata().getSchema();
		return schema.createClass(values.get("name").toString());
	}

	@Override
	protected Class<OClass> getMainInterface() {
		return OClass.class;
	}
	
	public static OClass newPrototype()
	{
		return newPrototype(new OClassPrototyper());
	}
	
	
	

	@Override
	protected Class<?>[] getAdditionalInterfaces() {
		return FIX_INTERFACES;
	}
	
	

	@Override
	protected void handleSet(String propName, Object value) {
		if("clusterSelection".equals(propName))
		{
			if(value instanceof OClusterSelectionStrategy)
			{
				super.handleSet(propName, value);
			}
			else if(value instanceof CharSequence)
			{
				OSchema schema = OrientDbWebSession.get().getDatabase().getMetadata().getSchema();
				
				if(schema instanceof OSchemaShared)
				{
					super.handleSet(propName, ((OSchemaShared)schema).getClusterSelectionFactory().newInstance(value.toString()));
				}
			}
		}
		else
		{
			super.handleSet(propName, value);
		}
	}

	@Override
	protected Object handleCustom(Object proxy, Method method, Object[] args) {
		String methodName = method.getName();
		if("properties".equals(methodName) || "declaredProperties".equals(methodName))
		{
			return Collections.EMPTY_SET;
		}
		else
		{
			return super.handleCustom(proxy, method, args);
		}
	}

	@Override
	public String toString() {
		return "Prototype for '"+getMainInterface().getName()+"'";
	}
	
	
	
	

}
