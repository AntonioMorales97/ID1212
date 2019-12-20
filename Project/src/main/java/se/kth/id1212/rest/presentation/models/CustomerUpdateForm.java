package se.kth.id1212.rest.presentation.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import se.kth.id1212.rest.enums.CustomerMembership;
import se.kth.id1212.rest.validation.ValueOfEnum;

/**
 * Represents a form to update <code>Customer</code>s. For now, only the membership of the <code>Customer</code>
 * can be updated.
 * 
 * @author Antonio
 *
 */
public class CustomerUpdateForm {
	@NotNull(message = "Customer membership is missing")
	@NotBlank(message = "Customer membership is blank")
	@ValueOfEnum(enumClass = CustomerMembership.class, message = "Not supported customer membership")
	private String membership;

	/**
	 * Sets the membership to the new given membership in upper case.
	 * 
	 * @param membership The new given membership.
	 */
	public void setMembership(String membership) {
		this.membership = membership.toUpperCase();
	}

	/**
	 * Gets the membership.
	 * 
	 * @return the membership.
	 */
	public String getMembership() {
		return this.membership;
	}
}
