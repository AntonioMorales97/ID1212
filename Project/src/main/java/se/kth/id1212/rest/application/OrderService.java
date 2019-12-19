package se.kth.id1212.rest.application;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.kth.id1212.rest.domain.Customer;
import se.kth.id1212.rest.domain.Order;
import se.kth.id1212.rest.enums.OrderStatus;
import se.kth.id1212.rest.presentation.error.CustomerNotFoundException;
import se.kth.id1212.rest.presentation.error.IllegalTransactionException;
import se.kth.id1212.rest.presentation.error.OrderNotFoundException;
import se.kth.id1212.rest.repository.CustomerRepository;
import se.kth.id1212.rest.repository.OrderRepository;

@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
public class OrderService implements IOrderService {

	@Autowired
	private OrderRepository orderRepo;
	
	@Autowired
	private CustomerRepository customerRepo;
	
	/**
	 * @return all the <code>Order</code>s stored in the database.
	 */
	@Override
	public List<Order> getAllOrders(){
		return orderRepo.findAll();
	}

	/**
	 * @param id The ID of a <code>Customer</code>.
	 * @return all the <code>Order</code>s of the customer.
	 */
	@Override
	public List<Order> getAllOrdersForCustomer(Long id){
		return orderRepo.findAllByCustomerId(id);
	}

	/**
	 * @param id The ID of an <code>Order</code>.
	 * @return the <code>Order</code>.
	 */
	@Override
	public Order getOrderById(Long id) {
		return findOrderById(id);
	}

	/**
	 * Add a new <code>Order</code> for a <code>Customer</code>.
	 * 
	 * @param id The ID of the <code>Customer</code>.
	 * @param status The status of the <code>Order</code>.
	 * @param price The price of the <code>Order</code>.
	 * @param description The <code>Order</code> description.
	 * @return the created <code>Order</code>.
	 */
	@Override
	public Order addOrderToCustomer(Long id, OrderStatus status, Double price, String description) {
		Customer customer = findCustomerById(id);
		if(!customer.isVerified())
			throw new IllegalTransactionException("Customer must be verified before adding orders!");
		Order order = new Order(status, price, description, customer);
		customer.getOrders().add(order);
		return order;
	}

	/**
	 * Updates the status of an <code>Order</code>.
	 * 
	 * @param id The ID of the <code>Order</code>.
	 * @param status The new <code>OrderStatus</code> to be updated to.
	 * @return the updated <code>Order</code>.
	 */
	@Override
	public Order updateOrderStatus(Long id, OrderStatus status) {
		Order order = findOrderById(id);
		order.setStatus(status);
		return order;
	}

	/**
	 * Deletes an <code>Order</code> with the given ID.
	 * 
	 * @param id The ID of the <code>Order</code> to be deleted.
	 */
	@Override
	public void deleteOrder(Long id) {
		Order order = findOrderById(id);
		if(order.isRemovable()) {
			if(order.getCustomer().getOrders().remove(order))
				return;
			throw new RuntimeException("Ops, order could not be removed :("); //should never come here
		}
		throw new IllegalTransactionException("Cannot delete an order that has status: " + order.getStatus());
	}
	
	private Order findOrderById(Long id) {
		Optional<Order> order = this.orderRepo.findById(id);
		if(order.isPresent())
			return order.get();

		throw new OrderNotFoundException("The order with the id: " + id + ", was not found!");
	}

	private Customer findCustomerById(Long id) {
		Optional<Customer> customer = this.customerRepo.findById(id);
		if(customer.isPresent())
			return customer.get();

		throw new CustomerNotFoundException("The customer with the id: " + id + ", was not found!");	
	}
}
