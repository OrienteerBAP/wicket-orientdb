package ru.ydn.wicket.wicketorientdb.validation;

import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchemaShared;

/**
 * Validation of names for schema related objects, for example: {@link OClass}, {@link OProperty}, etc.
 */
public class OSchemaNamesValidator implements IValidator<String>{
	private static final long serialVersionUID = 1L;
	public static final OSchemaNamesValidator CLASS_NAME_VALIDATOR = new OSchemaNamesValidator(true, false);
	public static final OSchemaNamesValidator FIELD_NAME_VALIDATOR = new OSchemaNamesValidator(false, true);
	public static final OSchemaNamesValidator ALL_VALIDATOR = new OSchemaNamesValidator(true, true);
	
	private final boolean checkClassNameValidity;
	private final boolean checkFieldNameValidity;
	
	public OSchemaNamesValidator(boolean checkClassNameValidity, boolean checkFieldNameValidity)
	{
		this.checkClassNameValidity = checkClassNameValidity;
		this.checkFieldNameValidity = checkFieldNameValidity;
	}
	
	@Override
	public void validate(IValidatable<String> validatable) {
		String value = validatable.getValue();
		if(Strings.isEmpty(value))
		{
			validatable.error(new ValidationError(this, "empty"));
		}
		else
		{
			Character ch = null;
			if(checkClassNameValidity) ch = OSchemaShared.checkClassNameIfValid(value);
			if(ch==null && checkFieldNameValidity) ch = OSchemaShared.checkFieldNameIfValid(value); 
			if(ch!=null)
			{
				ValidationError error = new ValidationError(this, "invalidchar");
				error.setVariable("char", ch);
				validatable.error(error);
			}
		}
	}

}
