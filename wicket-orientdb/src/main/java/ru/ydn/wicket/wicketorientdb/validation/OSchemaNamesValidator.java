package ru.ydn.wicket.wicketorientdb.validation;

import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import com.orientechnologies.orient.core.metadata.schema.OSchemaShared;

public class OSchemaNamesValidator implements IValidator<String>{
	public static final OSchemaNamesValidator INSTANCE = new OSchemaNamesValidator();
	
	@Override
	public void validate(IValidatable<String> validatable) {
		String value = validatable.getValue();
		if(Strings.isEmpty(value))
		{
			validatable.error(new ValidationError(this, "empty"));
		}
		else
		{
			Character ch = OSchemaShared.checkNameIfValid(value);
			if(ch!=null)
			{
				ValidationError error = new ValidationError(this, "invalidchar");
				error.setVariable("char", ch);
				validatable.error(error);
			}
		}
	}

}
