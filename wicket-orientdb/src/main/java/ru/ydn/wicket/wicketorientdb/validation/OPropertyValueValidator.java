package ru.ydn.wicket.wicketorientdb.validation;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.validation.INullAcceptingValidator;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import ru.ydn.wicket.wicketorientdb.OrientDbWebSession;
import ru.ydn.wicket.wicketorientdb.model.OPropertyModel;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.db.record.OIdentifiable;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link IValidator} for validating of value agains constraints specified on {@link OProperty}
 * @param <T>
 */
public class OPropertyValueValidator<T> extends Behavior implements
		IValidator<T>, INullAcceptingValidator<T> {
	private static final long serialVersionUID = 1L;
	private Component component;
	private IModel<OProperty> propertyModel;
	
	public OPropertyValueValidator(OProperty property) {
		this(new OPropertyModel(property));
	}

	public OPropertyValueValidator(IModel<OProperty> propertyModel) {
		this.propertyModel = propertyModel;
	}

	public OProperty getProperty() {
		return propertyModel.getObject();
	}

	@Override
	public void validate(IValidatable<T> validatable) {
		T fieldValue = validatable.getValue();
		OProperty p = getProperty();
		if (fieldValue == null) {
			if (p.isNotNull()) {
				validatable.error(newValidationError("required"));
			}
		} else {
			OType type = p.getType();
			switch (type) {
			case LINK:
				validateLink(validatable, p, fieldValue);
				break;
			case LINKLIST:
				if (!(fieldValue instanceof List))
					validatable.error(newValidationError("wrongtype"));
				else if (p.getLinkedClass() != null)
					for (Object item : ((List<?>) fieldValue))
						validateLink(validatable, p, item);
				break;
			case LINKSET:
				if (!(fieldValue instanceof Set))
					validatable.error(newValidationError("wrongtype"));
				else if (p.getLinkedClass() != null)
					for (Object item : ((Set<?>) fieldValue))
						validateLink(validatable, p, item);
				break;
			case LINKMAP:
				if (!(fieldValue instanceof Map))
					validatable.error(newValidationError("wrongtype"));
				else if (p.getLinkedClass() != null)
					for (Entry<?, ?> entry : ((Map<?, ?>) fieldValue)
							.entrySet())
						validateLink(validatable, p, entry.getValue());
				break;

			case EMBEDDED:
				validateEmbedded(validatable, p, fieldValue);
				break;
			case EMBEDDEDLIST:
				if (!(fieldValue instanceof List))
					validatable.error(newValidationError("wrongtype"));
				else if (p.getLinkedClass() != null) {
					for (Object item : ((List<?>) fieldValue))
						validateEmbedded(validatable, p, item);
				} else if (p.getLinkedType() != null) {
					for (Object item : ((List<?>) fieldValue))
						validateType(validatable, p, item);
				}
				break;
			case EMBEDDEDSET:
				if (!(fieldValue instanceof Set))
					validatable.error(newValidationError("wrongtype"));
				else if (p.getLinkedClass() != null) {
					for (Object item : ((Set<?>) fieldValue))
						validateEmbedded(validatable, p, item);
				} else if (p.getLinkedType() != null) {
					for (Object item : ((Set<?>) fieldValue))
						validateType(validatable, p, item);
				}
				break;
			case EMBEDDEDMAP:
				if (!(fieldValue instanceof Map))
					validatable.error(newValidationError("wrongtype"));
				else if (p.getLinkedClass() != null) {
					for (Entry<?, ?> entry : ((Map<?, ?>) fieldValue)
							.entrySet())
						validateEmbedded(validatable, p,
								entry.getValue());
				} else if (p.getLinkedType() != null) {
					for (Entry<?, ?> entry : ((Map<?, ?>) fieldValue)
							.entrySet())
						validateType(validatable, p, entry.getValue());
				}
				break;
			default:
				break;
			}
			if (p.getMin() != null) {
			      // MIN
			      final String min = p.getMin();

			      if (p.getType().equals(OType.STRING) && (fieldValue != null && ((String) fieldValue).length() < Integer.parseInt(min)))
			    	  validatable.error(newValidationError("minviolationString", "min", min));
			      else if (p.getType().equals(OType.BINARY) && (fieldValue != null && ((byte[]) fieldValue).length < Integer.parseInt(min)))
			    	  validatable.error(newValidationError("minviolationBin", "min", min));
			      else if (p.getType().equals(OType.INTEGER) && (fieldValue != null && type.asInt(fieldValue) < Integer.parseInt(min)))
			    	  validatable.error(newValidationError("minviolation", "min", min));
			      else if (p.getType().equals(OType.LONG) && (fieldValue != null && type.asLong(fieldValue) < Long.parseLong(min)))
			    	  validatable.error(newValidationError("minviolation", "min", min));
			      else if (p.getType().equals(OType.FLOAT) && (fieldValue != null && type.asFloat(fieldValue) < Float.parseFloat(min)))
			    	  validatable.error(newValidationError("minviolation", "min", min));
			      else if (p.getType().equals(OType.DOUBLE) && (fieldValue != null && type.asDouble(fieldValue) < Double.parseDouble(min)))
			    	  validatable.error(newValidationError("minviolation", "min", min));
			      else if (p.getType().equals(OType.DATE)) {
			        try {
			          if (fieldValue != null
			              && ((Date) fieldValue).before(getDatabase().getStorage().getConfiguration().getDateFormatInstance()
			                  .parse(min)))
			        	  validatable.error(newValidationError("minviolationDate", "min", min));
			        } catch (ParseException e) {
			        }
			      } else if (p.getType().equals(OType.DATETIME)) {
			        try {
			          if (fieldValue != null
			              && ((Date) fieldValue).before(getDatabase().getStorage().getConfiguration().getDateTimeFormatInstance()
			                  .parse(min)))
			        	  validatable.error(newValidationError("minviolationDate", "min", min));
			        } catch (ParseException e) {
			        }
			      } else if ((p.getType().equals(OType.EMBEDDEDLIST) || p.getType().equals(OType.EMBEDDEDSET)
			          || p.getType().equals(OType.LINKLIST) || p.getType().equals(OType.LINKSET))
			          && (fieldValue != null && ((Collection<?>) fieldValue).size() < Integer.parseInt(min)))
			    	  validatable.error(newValidationError("minviolationCollection", "min", min));
			    }

			    if (p.getMax() != null) {
			      // MAX
			      final String max = p.getMax();

			      if (p.getType().equals(OType.STRING) && (fieldValue != null && ((String) fieldValue).length() > Integer.parseInt(max)))
			    	  validatable.error(newValidationError("maxviolationString", "max", max));
			      else if (p.getType().equals(OType.BINARY) && (fieldValue != null && ((byte[]) fieldValue).length > Integer.parseInt(max)))
			    	  validatable.error(newValidationError("maxviolationBin", "max", max));
			      else if (p.getType().equals(OType.INTEGER) && (fieldValue != null && type.asInt(fieldValue) > Integer.parseInt(max)))
			    	  validatable.error(newValidationError("maxviolation", "max", max));
			      else if (p.getType().equals(OType.LONG) && (fieldValue != null && type.asLong(fieldValue) > Long.parseLong(max)))
			    	  validatable.error(newValidationError("maxviolation", "max", max));
			      else if (p.getType().equals(OType.FLOAT) && (fieldValue != null && type.asFloat(fieldValue) > Float.parseFloat(max)))
			    	  validatable.error(newValidationError("maxviolation", "max", max));
			      else if (p.getType().equals(OType.DOUBLE) && (fieldValue != null && type.asDouble(fieldValue) > Double.parseDouble(max)))
			    	  validatable.error(newValidationError("maxviolation", "max", max));
			      else if (p.getType().equals(OType.DATE)) {
			        try {
			          if (fieldValue != null
			              && ((Date) fieldValue).before(getDatabase().getStorage().getConfiguration().getDateFormatInstance()
			                  .parse(max)))
			        	  validatable.error(newValidationError("maxviolationDate", "max", max));
			        } catch (ParseException e) {
			        }
			      } else if (p.getType().equals(OType.DATETIME)) {
			        try {
			          if (fieldValue != null
			              && ((Date) fieldValue).before(getDatabase().getStorage().getConfiguration().getDateTimeFormatInstance()
			                  .parse(max)))
			        	  validatable.error(newValidationError("maxviolationDate", "max", max));
			        } catch (ParseException e) {
			        }
			      } else if ((p.getType().equals(OType.EMBEDDEDLIST) || p.getType().equals(OType.EMBEDDEDSET)
			          || p.getType().equals(OType.LINKLIST) || p.getType().equals(OType.LINKSET))
			          && (fieldValue != null && ((Collection<?>) fieldValue).size() > Integer.parseInt(max)))
			    	  validatable.error(newValidationError("maxviolationCollection", "max", max));
			    }			
		}
	}

	protected void validateLink(final IValidatable<T> validatable,
			final OProperty p, final Object linkValue) {
		if (linkValue == null)
			validatable.error(newValidationError("nulllink"));
		else {
			ORecord linkedRecord = null;
			if (linkValue instanceof OIdentifiable)
				linkedRecord = ((OIdentifiable) linkValue).getRecord();
			else if (linkValue instanceof String)
				linkedRecord = new ORecordId((String) linkValue).getRecord();
			else
				validatable.error(newValidationError("linkwrong"));

			if (linkedRecord != null && p.getLinkedClass() != null) {
				if (!(linkedRecord instanceof ODocument))
					validatable.error(newValidationError("linktypewrong",
							"linkedClass", p.getLinkedClass(), "identity",
							linkedRecord.getIdentity()));

				final ODocument doc = (ODocument) linkedRecord;

				// AT THIS POINT CHECK THE CLASS ONLY IF != NULL BECAUSE IN CASE
				// OF GRAPHS THE RECORD COULD BE PARTIAL
				if (doc.getSchemaClass() != null
						&& !p.getLinkedClass().isSuperClassOf(
								doc.getSchemaClass()))
					validatable.error(newValidationError("linktypewrong",
							"linkedClass", p.getLinkedClass(), "identity",
							linkedRecord.getIdentity()));

			}
		}
	}

	protected void validateEmbedded(final IValidatable<T> validatable,
			final OProperty p, final Object fieldValue) {
		if (fieldValue instanceof ORecordId) {
			validatable.error(newValidationError("embeddedRecord"));
			return;
		} else if (fieldValue instanceof OIdentifiable) {
			if (((OIdentifiable) fieldValue).getIdentity().isValid()) {
				validatable.error(newValidationError("embeddedRecord"));
				return;
			}

			final OClass embeddedClass = p.getLinkedClass();
			if (embeddedClass != null) {
				final ORecord rec = ((OIdentifiable) fieldValue).getRecord();
				if (!(rec instanceof ODocument)) {
					validatable.error(newValidationError("embeddedNotDoc"));
					return;
				}

				final ODocument doc = (ODocument) rec;
				if (doc.getSchemaClass() == null
						|| !(doc.getSchemaClass().isSubClassOf(embeddedClass))) {
					validatable.error(newValidationError("embeddedWrongType", "expectedType", embeddedClass.getName()));
					return;
				}
			}

		} else {
			validatable.error(newValidationError("embeddedNotDoc"));
			return;
		}
	}

	protected void validateType(final IValidatable<T> validatable,
			final OProperty p, final Object value) {
		if (value != null)
			if (OType.convert(value, p.getLinkedType().getDefaultJavaType()) == null)
				validatable.error(newValidationError("wrongtype", "expectedType", p.getLinkedType().toString()));
	}

	protected ValidationError newValidationError(String variation,
			Object... params) {
		ValidationError error = new ValidationError(this, variation);
		error.setVariable("property", getProperty().getFullName());
		error.setVariable("type", getProperty().getType());
		for (int i = 0; i < params.length; i += 2) {
			error.setVariable(params[i].toString(), params[i + 1]);
		}
		return error;
	}
	
	protected ODatabaseDocumentInternal getDatabase()
	{
		return (ODatabaseDocumentInternal)OrientDbWebSession.get().getDatabase();
	}

	@Override
	public void detach(Component component) {
		super.detach(component);
		propertyModel.detach();
	}

	@Override
	public final void bind(final Component hostComponent) {
		Args.notNull(hostComponent, "hostComponent");

		if (component != null) {
			throw new IllegalStateException(
					"this kind of validator cannot be attached to "
							+ "multiple components; it is already attached to component "
							+ component + ", but component " + hostComponent
							+ " wants to be attached too");
		}

		component = hostComponent;
	}

	@Override
	public final void unbind(Component component) {
		this.component = null;

		super.unbind(component);
	}
}
