package ru.ydn.wicket.wicketorientdb.utils;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.core.util.lang.PropertyResolver.AbstractGetAndSet;
import org.apache.wicket.core.util.lang.PropertyResolver.IGetAndSet;
import org.apache.wicket.core.util.lang.PropertyResolver.IPropertyLocator;
import org.apache.wicket.core.util.lang.PropertyResolverConverter;

import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.type.ODocumentWrapper;

/**
 * {@link IPropertyLocator} for {@link ODocument} and related classes 
 */

public class ODocumentPropertyLocator implements IPropertyLocator {
	
	private IPropertyLocator locator;
	
	public ODocumentPropertyLocator(IPropertyLocator locator) {
		this.locator = locator;
	}

	@Override
	public IGetAndSet get(Class<?> clz, String exp) {
		if(exp == null || exp.isEmpty()) return locator.get(clz, exp);
		else if(exp.charAt(0)=='@') return locator.get(clz, exp.substring(1)); // Way to by pass getting property as field
		else if(OIdentifiable.class.isAssignableFrom(clz)) return new ODocumentGetAndSet(exp);
		else if(ODocumentWrapper.class.isAssignableFrom(clz)) {
			//If there is no default locator: lets address to ODocument
			IGetAndSet ret = null;
			try { ret = locator.get(clz, exp);} catch (Exception e) {/*NOP*/}
			return ret!=null?ret:new ODocumentGetAndSet(exp);
		} 
		else return locator.get(clz, exp);
	}
	
	/**
	 * {@link IGetAndSet} for {@link ODocument} 
	 */
	public static class ODocumentGetAndSet extends AbstractGetAndSet {
		
		private String exp;
		
		public ODocumentGetAndSet(String exp) {
			this.exp = exp;
		}
		
		protected ODocument toODocument(Object object) {
			if(object instanceof ODocument) return (ODocument) object;
			else if(object instanceof OIdentifiable) return ((OIdentifiable)object).getRecord();
			else if(object instanceof ODocumentWrapper) return ((ODocumentWrapper)object).getDocument();
			else throw new WicketRuntimeException("Object is not castable to ODocument: "+object);
		}

		@Override
		public Object getValue(Object object) {
			ODocument doc = toODocument(object);
			if(doc==null) return null;
			else {
				return doc.field(exp);
			}
		}

		@Override
		public Object newValue(Object object) {
			return null;
		}

		@Override
		public void setValue(Object object, Object value, PropertyResolverConverter converter) {
			ODocument doc = toODocument(object);
			if(value!=null && ! OType.isSimpleType(value)) { //Try to convert if type is not simple
				OClass schemaClass = doc.getSchemaClass();
				OProperty property = schemaClass.getProperty(exp);
				if(property!=null) {
					value = converter.convert(value, property.getType().getDefaultJavaType());
				}
			}
			doc.field(exp, value);
		}

	}

}
