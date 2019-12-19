package se.kth.id1212.rest.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
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

	private static final String SEQ_NAME_KEY = "SEQ_NAME";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME_KEY)
	@SequenceGenerator(name = SEQ_NAME_KEY, sequenceName = "VERIFICATION_TOKEN_SEQUENCE")
	private Long id;

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
