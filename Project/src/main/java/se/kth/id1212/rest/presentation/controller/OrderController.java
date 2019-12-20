package se.kth.id1212.rest.presentation.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import se.kth.id1212.rest.application.IOrderService;
import se.kth.id1212.rest.domain.Order;
import se.kth.id1212.rest.enums.OrderStatus;
import se.kth.id1212.rest.presentation.models.OrderForm;
import se.kth.id1212.rest.presentation.util.RepresentationAssembler;
import se.kth.id1212.rest.security.MyUserPrincipal;

/**
 * The REST API for the customer orders. The REST API can list all orders, retrieve specific orders,
 * create orders, update order status, and delete orders. Note that all the orders are related to one
 * customer only.
 * 
 * @author Antonio
 *
 */
@RestController
@Validated
public class OrderController {

	@Autowired
	private IOrderService orderService;

	@Autowired
	private RepresentationAssembler representationAssembler;

	/**
	 * List all <code>Order</code>s. No authentication required.
	 * 
	 * @return a <code>CollectionModel</code> with all the orders embedded.
	 */
	@GetMapping("/orders")
	public CollectionModel<Order> getAllOrders(){
		List<Order> orders = orderService.getAllOrders();
		representationAssembler.addLinksToOrders(orders);
		return new CollectionModel<Order>(orders, linkTo(methodOn(OrderController.class)
				.getAllOrders()).withSelfRel());
	}

	/**
	 * Retrieves an <code>Order</code> with a specific id. No authentication required.
	 * 
	 * @param id The ID of the <code>Order</code>.
	 * @return the <code>Order</code>.
	 */
	@GetMapping("/order/{id}")
	public Order getOrder(@PathVariable Long id) {
		Order order = orderService.getOrderById(id);
		representationAssembler.addLinksToOrder(order);
		return order;
	}

	/**
	 * Lists all the orders for a specific <code>Customer</code>. Authentication required.
	 * 
	 * @param customerId The ID of the <code>Customer</code>.
	 * @return a <code>CollectionModel</code> with all the orders of the <code>Customer</code>.
	 */
	@GetMapping("/customer/orders")
	public CollectionModel<Order> getOrdersForCustomer(@AuthenticationPrincipal MyUserPrincipal principal){
		List<Order> orders = orderService.getAllOrdersForCustomer(principal.getCustomerId());
		representationAssembler.addLinksToOrders(orders);	
		return new CollectionModel<Order>(orders, linkTo(methodOn(OrderController.class)
				.getAllOrders()).withSelfRel());
	}

	/**
	 * Creates an <code>Order</code> for a <code>Customer</code>. Authentication required.
	 * 
	 * @param customerId The <code>Customer</code>.
	 * @param orderForm The form used to create an <code>Order</code>.
	 * @return the created <code>Order</code>.
	 */
	@PostMapping("/customer/orders/add")
	@ResponseStatus(HttpStatus.CREATED)
	public Order addOrderToCustomer(@AuthenticationPrincipal MyUserPrincipal principal, @RequestBody @Valid OrderForm orderForm) {
		Order order = orderService.addOrderToCustomer(principal.getCustomerId(), OrderStatus.IN_PROGRESS, orderForm.getPrice(),
				orderForm.getDescription());
		representationAssembler.addLinksToOrder(order);
		return order;
	}

	/**
	 * Updates the status of an <code>Order</code> to completed. Authentication required.
	 * 
	 * @param orderId The ID of the <code>Order</code>.
	 * @return the updated <code>Order</code>.
	 */
	@PutMapping("/order/{orderId}/complete")
	public Order completeOrder(@AuthenticationPrincipal MyUserPrincipal principal, @PathVariable Long orderId) {
		Order order = orderService.updateOrderStatus(principal.getCustomer(), orderId, OrderStatus.COMPLETED);
		representationAssembler.addLinksToOrder(order);
		return order;
	}

	/**
	 * Updates the status of an <code>Order</code> to cancelled. Authentication required.
	 * 
	 * @param orderId The ID of the <code>Order</code>.
	 * @return the updated <code>Order</code>.
	 */
	@PutMapping("/order/{orderId}/cancel")
	public Order cancelOrder(@AuthenticationPrincipal MyUserPrincipal principal, @PathVariable Long orderId) {
		Order order = orderService.updateOrderStatus(principal.getCustomer(), orderId, OrderStatus.CANCELLED);
		representationAssembler.addLinksToOrder(order);
		return order;
	}

	/**
	 * Deletes an <code>Order</code>. Authentication required.
	 * 
	 * @param orderId The ID of the <code>Order</code>.
	 * @return an empty <code>ResponseEntity</code> with HTTP 204 if successful.
	 */
	@DeleteMapping("/order/{orderId}/delete")
	public ResponseEntity<?> deleteOrder(@AuthenticationPrincipal MyUserPrincipal principal, @PathVariable Long orderId) {
		orderService.deleteOrder(principal.getCustomer(), orderId);
		return ResponseEntity.noContent().build();
	}



}
