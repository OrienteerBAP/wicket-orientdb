package ru.ydn.wicket.wicketorientdb.utils;

import java.util.List;

import org.apache.wicket.util.lang.Objects;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * Utility to help create/update a schema
 * Main requirement: code writen with {@link OSchemaHelper} should be able to be executed several times without duplications and etc.
 */
public class OSchemaHelper
{
	protected ODatabaseDocument db;
	protected OSchema schema;
	
	protected OClass lastClass;
	protected OProperty lastProperty;
	protected OIndex<?> lastIndex;
	protected ODocument lastDocument;
	
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
		if(!Objects.equal(linkedToClass, lastProperty.getLinkedClass()))
		{
			lastProperty.setLinkedClass(linkedToClass);
		}
		return this;
	}
	
	public OSchemaHelper oIndex(INDEX_TYPE type)
	{
		checkOProperty();
		return oIndex(lastProperty.getFullName(), type);
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
	
	public OSchemaHelper oDocument()
	{
		checkOClass();
		lastDocument = new ODocument(lastClass);
		return this;
	}
	
	public OSchemaHelper oDocument(String pkField, Object pkValue)
	{
		checkOClass();
		List<ODocument> docs = db.query(new OSQLSynchQuery<ODocument>("select from "+lastClass.getName()+" where "+pkField+" = ?", 1), pkValue);
		if(docs!=null && !docs.isEmpty())
		{
			lastDocument = docs.get(0);
		}
		else
		{
			lastDocument = new ODocument(lastClass);
			lastDocument.field(pkField, pkValue);
		}
		return this;
	}
	
	public OSchemaHelper field(String field, Object value)
	{
		checkODocument();
		lastDocument.field(field, value);
		return this;
	}
	
	public OSchemaHelper saveDocument()
	{
		checkODocument();
		lastDocument.save();
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
	
	public ODocument getODocument() {
		return lastDocument;
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
	
	protected void checkODocument()
	{
		if(lastDocument==null) throw new IllegalStateException("Last ODocument should not be null");
	}
	
}
