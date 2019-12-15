package se.kth.id1212.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import se.kth.id1212.rest.domain.Customer;

/**
 * Holds the database access for the <code>Customer</code>s.
 * Supports creating, updating, deleting, and finding instances 
 * of <code>Customer</code>.
 * 
 * @author Antonio
 *
 */
public interface CustomerRepository extends JpaRepository<Customer, Long>{
	public Customer findByPersonalNumber(String personalNumber);
}
