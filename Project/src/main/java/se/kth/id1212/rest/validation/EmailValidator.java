package se.kth.id1212.rest.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * The validator to validate that an argument, supposed to be an email, is
 * indeed an email with valid format.
 * 
 * @author Antonio
 *
 */
public class EmailValidator implements ConstraintValidator<ValidEmail, String>{

	private Pattern pattern;

	private Matcher matcher;

	private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		pattern = Pattern.compile(EMAIL_REGEX);
		matcher = pattern.matcher(email);
		return matcher.matches();
	}

}
