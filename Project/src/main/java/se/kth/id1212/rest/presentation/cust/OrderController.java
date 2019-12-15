package se.kth.id1212.rest.presentation.cust;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import se.kth.id1212.rest.application.CustomerOrderService;
import se.kth.id1212.rest.domain.Order;
import se.kth.id1212.rest.util.OrderStatus;

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
class OrderController {

	@Autowired
	private CustomerOrderService customerOrderService;

	@Autowired
	private RepresentationAssembler representationAssembler;

	/**
	 * List all <code>Order</code>s.
	 * 
	 * @return a <code>CollectionModel</code> with all the orders embedded.
	 */
	@GetMapping("/customers/orders")
	CollectionModel<Order> getAllOrders(){
		List<Order> orders = customerOrderService.getAllOrders();
		representationAssembler.addLinksToOrders(orders);
		return new CollectionModel<Order>(orders, linkTo(methodOn(OrderController.class)
				.getAllOrders()).withSelfRel());
	}

	/**
	 * Retrieves an <code>Order</code> with a specific id.
	 * 
	 * @param id The ID of the <code>Order</code>.
	 * @return the <code>Order</code>.
	 */
	@GetMapping("/customers/orders/{id}")
	Order getOrder(@PathVariable Long id) {
		Order order = customerOrderService.getOrderById(id);
		representationAssembler.addLinksToOrder(order);
		return order;
	}

	/**
	 * Lists all the orders for a specific <code>Customer</code>.
	 * 
	 * @param customerId The ID of the <code>Customer</code>.
	 * @return a <code>CollectionModel</code> with all the orders of the <code>Customer</code>.
	 */
	@GetMapping("/customers/{customerId}/orders")
	CollectionModel<Order> getOrdersForCustomer(@PathVariable Long customerId){
		List<Order> orders = customerOrderService.getAllOrdersForCustomer(customerId);
		representationAssembler.addLinksToOrders(orders);	
		return new CollectionModel<Order>(orders, linkTo(methodOn(OrderController.class)
				.getAllOrders()).withSelfRel());
	}

	/**
	 * Creates an <code>Order</code> for a <code>Customer</code>.
	 * 
	 * @param customerId The <code>Customer</code>.
	 * @param orderForm The form used to create an <code>Order</code>.
	 * @return the created <code>Order</code>.
	 */
	@PostMapping("/customers/{customerId}/orders")
	@ResponseStatus(HttpStatus.CREATED)
	Order addOrderToCustomer(@PathVariable Long customerId, @RequestBody @Valid OrderForm orderForm) {
		Order order = customerOrderService.addOrderToCustomer(customerId, OrderStatus.IN_PROGRESS, orderForm.getPrice(),
				orderForm.getDescription());
		representationAssembler.addLinksToOrder(order);
		return order;
	}

	/**
	 * Updates the status of an <code>Order</code> to completed.
	 * 
	 * @param orderId The ID of the <code>Order</code>.
	 * @return the updated <code>Order</code>.
	 */
	@PutMapping("/customers/orders/{orderId}/complete")
	Order completeOrder(@PathVariable Long orderId) {
		Order order = customerOrderService.updateOrderStatus(orderId, OrderStatus.COMPLETED);
		representationAssembler.addLinksToOrder(order);
		return order;
	}

	/**
	 * Updates the status of an <code>Order</code> to cancelled.
	 * 
	 * @param orderId The ID of the <code>Order</code>.
	 * @return the updated <code>Order</code>.
	 */
	@PutMapping("/customers/orders/{orderId}/cancel")
	Order cancelOrder(@PathVariable Long orderId) {
		Order order = customerOrderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
		representationAssembler.addLinksToOrder(order);
		return order;
	}

	/**
	 * Deletes an <code>Order</code>.
	 * 
	 * @param orderId The ID of the <code>Order</code>.
	 * @return an empty <code>ResponseEntity</code> with HTTP 204 if successful.
	 */
	@DeleteMapping("/customers/orders/{orderId}/delete")
	ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
		customerOrderService.deleteOrder(orderId);
		return ResponseEntity.noContent().build();
	}



}
