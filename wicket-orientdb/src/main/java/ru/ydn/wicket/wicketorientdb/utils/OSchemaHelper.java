package ru.ydn.wicket.wicketorientdb.utils;

import java.util.Objects;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;

public class OSchemaHelper
{
	protected ODatabaseDocument db;
	protected OSchema schema;
	
	protected OClass lastClass;
	protected OProperty lastProperty;
	protected OIndex<?> lastIndex;
	
	protected OSchemaHelper(ODatabaseDocument db)
	{
		this.db = db;
		this.schema = db.getMetadata().getSchema();
	}
	
	public static OSchemaHelper bind()
	{
		return new OSchemaHelper(OrientDbWebSession.get().getDatabase());
	}
	
	public static OSchemaHelper bind(ODatabaseDocument db)
	{
		return new OSchemaHelper(db);
	}
	
	public OSchemaHelper oClass(String className)
	{
		lastClass = schema.getClass(className);
		if(lastClass==null)
		{
			lastClass = schema.createClass(className);
		}
		lastProperty = null;
		lastIndex = null;
		return this;
	}
	
	public boolean existsClass(String className)
	{
		return schema.existsClass(className);
	}
	
	public OSchemaHelper oProperty(String propertyName, OType type)
	{
		checkOClass();
		lastProperty = lastClass.getProperty(propertyName);
		if(lastProperty==null)
		{
			lastProperty = lastClass.createProperty(propertyName, type);
		}
		else
		{
			if(!type.equals(lastProperty.getType()))
			{
				lastProperty.setType(type);
			}
		}
		return this;
	}
	
	public OSchemaHelper linkedClass(String className)
	{
		checkOProperty();
		OClass linkedToClass =  schema.getClass(className);
		if(linkedToClass==null) throw new IllegalArgumentException("Target OClass '"+className+"' to link to not found");
		if(!Objects.equals(linkedToClass, lastProperty.getLinkedClass()))
		{
			lastProperty.setLinkedClass(linkedToClass);
		}
		return this;
	}
	
	public OSchemaHelper oIndex(String name, INDEX_TYPE type)
	{
		checkOProperty();
		return oIndex(name, type, lastProperty.getName());
	}
	
	public OSchemaHelper oIndex(String name, INDEX_TYPE type, String... fields)
	{
		checkOClass();
		lastIndex = lastClass.getClassIndex(name);
		if(lastIndex==null)
		{
			lastIndex = lastClass.createIndex(name, type, fields);
		}
		else
		{
			//We can't do something to change type and fields if required
		}
		return this;
	}

	public OClass getOClass() {
		return lastClass;
	}

	public OProperty getOProperty() {
		return lastProperty;
	}

	public OIndex<?> getOIndex() {
		return lastIndex;
	}
	
	protected void checkOClass()
	{
		if(lastClass==null) throw new IllegalStateException("Last OClass should not be null");
	}
	
	protected void checkOProperty()
	{
		if(lastProperty==null) throw new IllegalStateException("Last OProperty should not be null");
	}
	
	protected void checkOIndex()
	{
		if(lastIndex==null) throw new IllegalStateException("Last OIndex should not be null");
	}
	
}
