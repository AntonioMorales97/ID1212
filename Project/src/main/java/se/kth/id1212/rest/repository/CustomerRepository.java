package se.kth.id1212.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import se.kth.id1212.rest.domain.Customer;

/**
 * Holds the database access for the <code>Customer</code>s.
 * Supports creating, updating, deleting, and finding <code>Customer</code>s.
 * 
 * @author Antonio
 *
 */
public interface CustomerRepository extends JpaRepository<Customer, Long>{
	/**
	 * Finds a <code>Customer</code> with the given personal number.
	 * 
	 * @param personalNumber The personal number of the <code>Customer</code>.
	 * @return a <code>Customer</code> with the given personal number if found; otherwise <code>null</code> is returned.
	 */
	public Customer findByPersonalNumber(String personalNumber);
}
