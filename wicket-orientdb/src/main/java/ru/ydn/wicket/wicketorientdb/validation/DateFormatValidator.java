package ru.ydn.wicket.wicketorientdb.validation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

/**
 * {@link IValidator} for a {@link DateFormat}s
 */
public class DateFormatValidator implements IValidator<String> {
	
	private static final long serialVersionUID = 1L;
	public static final DateFormatValidator SIMPLE_DATE_FORMAT_VALIDATOR = new DateFormatValidator(); 

	@Override
	public void validate(IValidatable<String> validatable) {
		String pattern = validatable.getValue();
		try {
			newDateFormat(pattern).format(new Date());
		} catch (Exception e) {
			ValidationError error = new ValidationError(this);
			error.setVariable("pattern", pattern);
			validatable.error(decorate(error, validatable));
		}
	}
	
	protected DateFormat newDateFormat(String pattern) {
		return new SimpleDateFormat(pattern);
	}
	
	protected IValidationError decorate(IValidationError error, IValidatable<String> validatable)
	{
		return error;
	}

}
