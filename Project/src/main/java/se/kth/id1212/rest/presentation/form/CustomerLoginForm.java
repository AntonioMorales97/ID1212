package se.kth.id1212.rest.presentation.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import se.kth.id1212.rest.validation.ValidEmail;

/**
 * A login form to login <code>Customer</code>s with email and password.
 * 
 * @author Antonio
 *
 */
@Data
public class CustomerLoginForm {
	@NotNull(message = "Email missing")
	@NotBlank(message = "Email cannot be blank")
	@ValidEmail
	private String email;

	@NotNull(message = "Password missing")
	@NotBlank(message = "Password cannot be blank")
	private String password;
}
