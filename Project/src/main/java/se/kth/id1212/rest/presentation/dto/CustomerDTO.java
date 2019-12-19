package se.kth.id1212.rest.presentation.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import se.kth.id1212.rest.enums.CustomerMembership;
import se.kth.id1212.rest.validation.ValidEmail;
import se.kth.id1212.rest.validation.ValueOfEnum;

/**
 * A <code>CustomerDTO</code> holding all the necessary information to later
 * create a {@link Customer}.
 * 
 * @author Antonio
 *
 */
@Data
public class CustomerDTO {
	@Pattern(regexp = "^[\\p{L}\\p{M}*]*$", message = "Only letters are allowed")
	@NotNull(message = "{customerDto.firstName.missing}")
	@NotBlank(message = "{customerDto.firstName.blank}")
	private String firstName;

	@Pattern(regexp = "^[\\p{L}\\p{M}*]*$", message = "Only letters are allowed")
	@NotNull(message = "{customerDto.lastName.missing}")
	@NotBlank(message = "{customerDto.lastName.blank}")
	private String lastName;

	@NotBlank(message = "{customerDto.personalNumber.blank}")
	@NotNull(message = "{customerDto.personalNumber.missing}")
	@Pattern(regexp="[\\d]{10}", message = "Personal number must be 10 digits")
	private String personalNumber;

	@NotNull(message = "{customerDto.age.missing}")
	@PositiveOrZero(message = "Age must be zero or greater")
	private Integer age;

	@Pattern(regexp = "^[\\p{L}\\p{M}*]*$", message = "Only letters are allowed")
	@NotNull(message = "{customerDto.membership.missing}")
	@NotBlank(message = "{customerDto.membership.blank}")
	@ValueOfEnum(enumClass = CustomerMembership.class, message = "Not supported customer membership")
	@Setter(AccessLevel.NONE) //we need to manually convert to upper case
	private String membership;

	@ValidEmail
	@NotNull(message = "{customerDto.email.missing}")
	@NotBlank(message = "{customerDto.email.blank}")
	private String email;

	@NotNull(message = "{customerDto.password.missing}")
	@NotBlank(message = "{customerDto.password.blank}")
	private String password;

	/**
	 * Sets the membership to upper case.
	 * 
	 * @param membership the membership of the customer.
	 */
	public void setMembership(String membership) {
		this.membership = membership.toUpperCase();
	}
}
