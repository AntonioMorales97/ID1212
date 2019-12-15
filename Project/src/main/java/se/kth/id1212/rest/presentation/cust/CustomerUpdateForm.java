package se.kth.id1212.rest.presentation.cust;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import se.kth.id1212.rest.util.CustomerMembership;
import se.kth.id1212.rest.util.ValueOfEnum;

/**
 * Simple form class to update <code>Customer</code>s. For now, only the membership of the customer
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
	
	public void setMembership(String membership) {
		this.membership = membership.toUpperCase();
	}
	
	public String getMembership() {
		return this.membership;
	}
}
