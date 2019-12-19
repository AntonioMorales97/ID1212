package se.kth.id1212.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import se.kth.id1212.rest.domain.Customer;
import se.kth.id1212.rest.domain.VerificationToken;

/**
 * Holds the database access for the <code>VerificationToken</code>s.
 * Supports creating, updating, deleting, and finding <code>VerificationToken</code>s.
 * 
 * @author Antonio
 *
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	/**
	 * Searches for <code>VerificationToken</code> with the given token.
	 * 
	 * @param token The token.
	 * @return the found <code>VerificationToken</code> if found; <code>null</code> otherwise.
	 */
	public VerificationToken findByToken(String token);

	/**
	 * Deletes a <code>VerificationToken</code> that has the given <code>Customer</code>.
	 * 
	 * @param customer The <code>Customer</code>.
	 */
	public void deleteByCustomer(Customer customer);
}
