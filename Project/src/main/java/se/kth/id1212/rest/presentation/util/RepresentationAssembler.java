package se.kth.id1212.rest.presentation.util;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.stereotype.Component;

import se.kth.id1212.rest.domain.Customer;
import se.kth.id1212.rest.domain.Order;
import se.kth.id1212.rest.enums.OrderStatus;
import se.kth.id1212.rest.presentation.controller.CustomerController;
import se.kth.id1212.rest.presentation.controller.OrderController;

/**
 * A helper class that uses Spring HATEOAS and helps adding
 * hypermedia such as links to <code>Customer</code>s and <code>Order</code>s etc.
 * 
 * @author Antonio
 *
 */
@Component
public class RepresentationAssembler {

	/**
	 * Add links to all the given <code>Order</code>s.
	 * 
	 * @param orders A <code>List</code> containing the <code>Order</code>s.
	 */
	public void addLinksToOrders(List<Order> orders) {
		orders.forEach(order -> addLinkToOneOrder(order));
	}

	/**
	 * Add links to the given <code>Order</code>.
	 * 
	 * @param order The <code>Order</code>.
	 */
	public void addLinksToOrder(Order order) {
		addLinkToOneOrder(order);
	}

	/**
	 * Add links to all the given <code>Customer</code>s.
	 * 
	 * @param customers A <code>List</code> containing the <code>Customer</code>s.
	 */
	public void addLinksToCustomers(List<Customer> customers){
		customers.forEach(customer -> addLinkToOneCustomer(customer));
	}

	/**
	 * Add links to the given <code>Customer</code>.
	 * 
	 * @param customer The <code>Customer</code>.
	 */
	public void addLinkToCustomer(Customer customer) {
		addLinkToOneCustomer(customer);
	}

	private void addLinkToOneCustomer(Customer customer) {
		customer.add(linkTo(methodOn(CustomerController.class).getCustomer(customer.getId())).withSelfRel());
		customer.add(linkTo(methodOn(CustomerController.class).getAllCustomers()).withRel("customers"));
		if(customer.getOrders().size() > 0) {
			customer.add(linkTo(methodOn(OrderController.class).getOrdersForCustomer(customer.getId())).withRel("orders"));
		}
	}

	private void addLinkToOneOrder(Order order) {
		order.add(linkTo(methodOn(OrderController.class).getOrder(order.getId())).withSelfRel());
		order.add(linkTo(methodOn(CustomerController.class).getCustomer(order.getCustomer().getId())).withRel("customer"));
		order.add(linkTo(methodOn(OrderController.class).getAllOrders()).withRel("orders"));
		if(order.getStatus() == OrderStatus.IN_PROGRESS) {
			order.add(linkTo(methodOn(OrderController.class).completeOrder(order.getId())).withRel("complete"));
			order.add(linkTo(methodOn(OrderController.class).cancelOrder(order.getId())).withRel("cancel"));
		}

		if(order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.COMPLETED) {
			order.add(linkTo(methodOn(OrderController.class).deleteOrder(order.getId())).withRel("delete"));
		}
	}
}
