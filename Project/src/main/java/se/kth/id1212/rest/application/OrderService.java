package se.kth.id1212.rest.application;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.kth.id1212.rest.application.exception.CustomerNotFoundException;
import se.kth.id1212.rest.application.exception.IllegalTransactionException;
import se.kth.id1212.rest.application.exception.OrderNotFoundException;
import se.kth.id1212.rest.domain.Customer;
import se.kth.id1212.rest.domain.Order;
import se.kth.id1212.rest.enums.OrderStatus;
import se.kth.id1212.rest.repository.CustomerRepository;
import se.kth.id1212.rest.repository.OrderRepository;

/**
 * Implements <code>IOrderService</code> with all the services related to <code>Order</code>s.
 * 
 * @author Antonio
 *
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
public class OrderService implements IOrderService {

	@Autowired
	private OrderRepository orderRepo;

	@Autowired
	private CustomerRepository customerRepo;

	@Override
	public List<Order> getAllOrders(){
		return orderRepo.findAll();
	}

	@Override
	public List<Order> getAllOrdersForCustomer(Long id){
		return orderRepo.findAllByCustomerId(id);
	}

	@Override
	public Order getOrderById(Long id) {
		return findOrderById(id);
	}

	@Override
	public Order addOrderToCustomer(Long id, OrderStatus status, Double price, String description) {
		Customer customer = findCustomerById(id);
		if(!customer.isVerified())
			throw new IllegalTransactionException("Customer must be verified before adding orders!");
		Order order = new Order(status, price, description, customer);
		customer.getOrders().add(order);
		return order;
	}

	@Override
	public Order updateOrderStatus(Customer customer, Long id, OrderStatus status) {
		Order order = findOrderById(id);
		if(!order.getCustomer().equals(customer))
			throw new IllegalTransactionException("Cannot update another customer's order status!");
		order.setStatus(status);
		return order;
	}

	@Override
	public void deleteOrder(Customer customer, Long id) {
		Order order = findOrderById(id);
		if(!order.getCustomer().equals(customer))
			throw new IllegalTransactionException("Cannot delete another customer's order!");
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
