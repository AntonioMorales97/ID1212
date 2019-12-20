package se.kth.id1212.rest.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Represents a verification token for verifying <code>Customer</code>'s email.
 * 
 * @author Antonio
 *
 */
@Data
@Entity
public class VerificationToken {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	private String token;

	@OneToOne(targetEntity = Customer.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "customer_id")
	@NotNull
	private Customer customer;

	protected VerificationToken() {}

	/**
	 * Creates a <code>VerificationToken</code>.
	 * 
	 * @param token The token generated.
	 * @param customer The <code>Customer</code> attached to this token.
	 */
	public VerificationToken(String token, Customer customer) {
		this.token = token;
		this.customer = customer;
	}

}
