package se.kth.id1212.rest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import se.kth.id1212.rest.domain.Order;

/**
 * Holds the database access for the <code>Order</code>s.
 * Supports creating, updating, deleting, and finding <code>Order</code>s.
 * 
 * @author Antonio
 *
 */
public interface OrderRepository extends JpaRepository<Order, Long>{
	/**
	 * Finds all the <code>Order</code>s for a <code>Customer</code>.
	 * 
	 * @param customerId The <code>Customer</code>.
	 * @return a <code>List</code> containing all the <code>Order</code> made by the given <code>Customer</code>.
	 */
	public List<Order> findAllByCustomerId(Long customerId);
}
