package ru.ydn.wicket.wicketorientdb.utils;

import java.util.List;
import java.util.function.Consumer;

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
	
	/**
	 * Create helper binded to a current {@link ODatabaseDocument}
	 * @return helper
	 */
	public static OSchemaHelper bind()
	{
		return new OSchemaHelper(OrientDbWebSession.get().getDatabase());
	}
	
	/**
	 * Create helper binded to specified {@link ODatabaseDocument}
	 * @param db {@link ODatabaseDocument} to bind to
	 * @return helper
	 */
	public static OSchemaHelper bind(ODatabaseDocument db)
	{
		return new OSchemaHelper(db);
	}
	
	/**
	 * Create if required {@link OClass}
	 * @param className name of a class to create
	 * @param superClasses list of superclasses
	 * @return this helper
	 */
	public OSchemaHelper oClass(String className, String... superClasses)
	{
		return oClass(className, false, superClasses);
	}
	
	/**
	 * Create if required abstract {@link OClass}
	 * @param className name of a class to create
	 * @param superClasses list of superclasses
	 * @return this helper
	 */
	public OSchemaHelper oAbstractClass(String className, String... superClasses)
	{
		return oClass(className, true, superClasses);
	}
	
	/**
	 * Create if required {@link OClass}
	 * @param className name of a class to create
	 * @param abstractClass is this class abstract
	 * @param superClasses list of superclasses
	 * @return this helper
	 */
	private OSchemaHelper oClass(String className, boolean abstractClass, String... superClasses)
	{
		lastClass = schema.getClass(className);
		if(lastClass==null)
		{
			OClass[] superClassesArray = new OClass[superClasses.length];
			for (int i = 0; i < superClasses.length; i++) {
				String superClassName = superClasses[i];
				superClassesArray[i] = schema.getClass(superClassName);
			}
			lastClass = abstractClass ? schema.createAbstractClass(className, superClassesArray) 
									  : schema.createClass(className, superClassesArray);
		} else {
			boolean currentlyAbstract = lastClass.isAbstract();
			if(currentlyAbstract!=abstractClass) lastClass.setAbstract(abstractClass);
		}
		lastProperty = null;
		lastIndex = null;
		return this;
	}
	
	/**
	 * Check that class exists
	 * @param className name of a class to check existance
	 * @return true if class exists
	 */
	public boolean existsClass(String className)
	{
		return schema.existsClass(className);
	}
	
	/**
	 * Create {@link OProperty} if required 
	 * @param propertyName property name to create
	 * @param type type of property to create
	 * @return this helper
	 */
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
	
	/**
	 * Set linked class to a current property
	 * @param className class name to set as a linked class
	 * @return this helper
	 */
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
	
	/**
	 * Set linked type to a current property
	 * @param linkedType {@link OType} to set as a linked type
	 * @return this helper
	 */
	public OSchemaHelper linkedType(OType linkedType)
	{
		checkOProperty();
		if(!Objects.equal(linkedType, lastProperty.getLinkedType()))
		{
			lastProperty.setLinkedType(linkedType);
		}
		return this;
	}
	
	/**
	 * Set default value to a current property
	 * @param defaultValue default value to set
	 * @return this helper
	 */
	public OSchemaHelper defaultValue(String defaultValue)
	{
		checkOProperty();
		lastProperty.setDefaultValue(defaultValue);
		return this;
	}
	
	/**
	 * Set min value to a current property
	 * @param min minimal value
	 * @return this helper
	 */
	public OSchemaHelper min(String min)
	{
		checkOProperty();
		lastProperty.setMin(min);
		return this;
	}
	
	/**
	 * Set max value to a current property
	 * @param max maximum value
	 * @return this helper
	 */
	public OSchemaHelper max(String max)
	{
		checkOProperty();
		lastProperty.setMax(max);
		return this;
	}
	
	
	
	/**
	 * Mark last property not null flag
	 * @return this helper
	 */
	public OSchemaHelper notNull()
	{
		return notNull(true);
	}
	
	/**
	 * Mark last property not null flag
	 * @param value value of not null flag
	 * @return this helper
	 */
	public OSchemaHelper notNull(boolean value)
	{
		checkOProperty();
		lastProperty.setNotNull(value);
		return this;
	}
	
	/**
	 * Set attr value for {@link OClass}
	 * @param attr attribute to set
	 * @param value value to set
	 * @return this helper
	 */
	public OSchemaHelper set(OClass.ATTRIBUTES attr, Object value) 
	{
		checkOClass();
		lastClass.set(attr, value);
		return this;
	}
	
	/**
	 * Set attr value for {@link OProperty}
	 * @param attr attribute to set
	 * @param value value to set
	 * @return this helper
	 */
	public OSchemaHelper set(OProperty.ATTRIBUTES attr, Object value) 
	{
		checkOProperty();
		lastProperty.set(attr, value);
		return this;
	}
	
	/**
	 * Create {@link OIndex} if required on a current property
	 * @param type type of an {@link OIndex}
	 * @return this helper
	 */
	public OSchemaHelper oIndex(INDEX_TYPE type)
	{
		checkOProperty();
		return oIndex(lastProperty.getFullName(), type);
	}
	
	/**
	 * Create {@link OIndex} if required on a current property
	 * @param name name of an index
	 * @param type type of an {@link OIndex}
	 * @return this helper
	 */
	public OSchemaHelper oIndex(String name, INDEX_TYPE type)
	{
		checkOProperty();
		return oIndex(name, type, lastProperty.getName());
	}
	
	/**
	 * Create {@link OIndex} on a set of fields if required
	 * @param name name of an index
	 * @param type type of an index
	 * @param fields fields to create index on
	 * @return this helper
	 */
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
	
	/**
	 * Create an {@link ODocument} of an current class
	 * @return this helper
	 */
	public OSchemaHelper oDocument()
	{
		checkOClass();
		lastDocument = new ODocument(lastClass);
		return this;
	}
	
	/**
	 * Create an {@link ODocument} if required of an current class.
	 * Existance of an document checked by specified primary key field name and required value
	 * @param pkField primary key field name
	 * @param pkValue required primary key value
	 * @return this helper
	 */
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
	
	/**
	 * Sets a field value for a current document
	 * @param field field name
	 * @param value value to set
	 * @return this helper
	 */
	public OSchemaHelper field(String field, Object value)
	{
		checkODocument();
		lastDocument.field(field, value);
		return this;
	}
	
	/**
	 * Save document
	 * @return this helper
	 */
	public OSchemaHelper saveDocument()
	{
		checkODocument();
		lastDocument.save();
		return this;
	}

	/**
	 * @return current {@link OClass}
	 */
	public OClass getOClass() {
		return lastClass;
	}
	
	/**
	 * Do actions on OClass
	 * @param consumer to perform actions
	 * @return this helper
	 */
	public OSchemaHelper doOnOClass(Consumer<OClass> consumer) {
		checkOClass();
		consumer.accept(getOClass());
		return this;
	}

	/**
	 * @return current {@link OProperty}
	 */
	public OProperty getOProperty() {
		return lastProperty;
	}
	
	/**
	 * Do actions on OProperty
	 * @param consumer to perform actions
	 * @return this helper
	 */
	public OSchemaHelper doOnOProperty(Consumer<OProperty> consumer) {
		checkOProperty();
		consumer.accept(getOProperty());
		return this;
	}

	/**
	 * @return current {@link OIndex}
	 */
	public OIndex<?> getOIndex() {
		return lastIndex;
	}
	
	/**
	 * Do actions on OIndex
	 * @param consumer to perform actions
	 * @return this helper
	 */
	public OSchemaHelper doOnOIndex(Consumer<OIndex<?>> consumer) {
		checkOIndex();
		consumer.accept(getOIndex());
		return this;
	}
	
	/**
	 * @return current {@link ODocument}
	 */
	public ODocument getODocument() {
		return lastDocument;
	}
	
	/**
	 * Do actions on ODocument
	 * @param consumer to perform actions
	 * @return this helper
	 */
	public OSchemaHelper doOnODocument(Consumer<ODocument> consumer) {
		checkODocument();
		consumer.accept(getODocument());
		return this;
	}
	
	/**
	 * @return binded database
	 */
	public ODatabaseDocument getDatabase() {
		return db;
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
