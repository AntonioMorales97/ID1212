package se.kth.id1212.rest.application;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.kth.id1212.rest.domain.Customer;
import se.kth.id1212.rest.domain.Order;
import se.kth.id1212.rest.exception.CustomerNotFoundException;
import se.kth.id1212.rest.exception.IllegalTransactionException;
import se.kth.id1212.rest.exception.OrderNotFoundException;
import se.kth.id1212.rest.repository.CustomerRepository;
import se.kth.id1212.rest.repository.OrderRepository;
import se.kth.id1212.rest.util.OrderStatus;

/**
 * Holds all the possible method calls for a client in this application and
 * passes it down to the domain and repository layers.
 * 
 * @author Antonio
 *
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
public class CustomerOrderService {

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private OrderRepository orderRepo;

	/**
	 * @return all <code>Customer</code>s stored in the database.
	 */
	public List<Customer> getAllCustomers(){
		return this.customerRepo.findAll();
	}

	/**
	 * Tries to find and get the <code>Customer</code> with the given id.
	 *  
	 * @param id The ID of the customer.
	 * @return the <code>Customer</code> if found.
	 */
	public Customer getCustomerById(Long id) {
		return findCustomerById(id);
	}

	/**
	 * Tries to find and get the <code>Customer</code> with the given personal number.
	 * 
	 * @param personalNumber The personal number of the customer.
	 * @return the <code>Customer</code> if found.
	 */
	public Customer getCustomerByPersonalNumber(String personalNumber) {
		Customer customer = this.customerRepo.findByPersonalNumber(personalNumber);
		if(customer != null)
			return customer;

		throw new CustomerNotFoundException("The customer with the personal number: " + personalNumber + " was not found!");
	}

	/**
	 * Tries to add a new <code>Customer</code>.
	 * 
	 * @param newCustomer the new <code>Customer</code> to be added.
	 * @return the added <code>Customer</code>.
	 */
	public Customer addCustomer(Customer newCustomer) {
		Customer customer = this.customerRepo.findByPersonalNumber(newCustomer.getPersonalNumber());
		if(customer == null)
			return this.customerRepo.save(newCustomer);

		throw new IllegalTransactionException("A customer with the given personal number already exists!");
	}

	/**
	 * Tries to update the membership of an existing <code>Customer</code>.
	 * 
	 * @param id The ID of the <code>Customer</code>.
	 * @param membership The new membership of the <code>Customer</code>.
	 * @return the updated <code>>Customer</code>.
	 */
	public Customer updateCustomerMembership(Long id, String membership) {
		Customer customer = findCustomerById(id);
		customer.setMembership(membership);
		return customer;
	}

	/**
	 * Tries to delete a <code>Customer</code> with the given ID.
	 * 
	 * @param id The ID of the <code>Customer</code>.
	 * @return the deleted <code>Customer</code> if found and deleted.
	 */
	public void deleteCustomer(Long id) {
		Customer customer = findCustomerById(id);
		if(customer.isRemovable()) {
			this.customerRepo.deleteById(id);
			return;
		}
		throw new IllegalTransactionException("Cannot delete a customer with orders that are in progress!");
	}

	public List<Order> getAllOrders(){
		return orderRepo.findAll();
	}

	public List<Order> getAllOrdersForCustomer(Long id){
		return orderRepo.findAllByCustomerId(id);
	}

	public Order getOrderById(Long id) {
		return findOrderById(id);
	}

	public Order addOrderToCustomer(Long id, OrderStatus status, Double price, String description) {
		Customer customer = findCustomerById(id);
		Order order = new Order(status, price, description, customer);
		customer.getOrders().add(order);
		return order;
	}
	
	public Order updateOrderStatus(Long id, OrderStatus status) {
		Order order = findOrderById(id);
		order.setStatus(status);
		return order;
	}
	
	public void deleteOrder(Long id) {
		Order order = findOrderById(id);
		if(order.getStatus() != OrderStatus.IN_PROGRESS) {
			if(order.getCustomer().getOrders().remove(order))
				return;
			throw new RuntimeException("Ops, order could not be removed :("); //should never come here
		}
		throw new IllegalTransactionException("Cannot delete an order that has status: " + order.getStatus());
	}

	private Customer findCustomerById(Long id) {
		Optional<Customer> customer = this.customerRepo.findById(id);
		if(customer.isPresent())
			return customer.get();

		throw new CustomerNotFoundException("The customer with the id: " + id + ", was not found!");	
	}
	
	private Order findOrderById(Long id) {
		Optional<Order> order = this.orderRepo.findById(id);
		if(order.isPresent())
			return order.get();
		
		throw new OrderNotFoundException("The order with the id: " + id + ", was not found!");
	}
}
