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

@RestController
@Validated
public class OrderController {
	
	@Autowired
	private CustomerOrderService customerOrderService;
	
	@Autowired
	private RepresentationAssembler representationAssembler;
	
	@GetMapping("/customers/orders")
	CollectionModel<Order> getAllOrders(){
		List<Order> orders = customerOrderService.getAllOrders();
		representationAssembler.addLinksToOrders(orders);
		return new CollectionModel<Order>(orders, linkTo(methodOn(OrderController.class)
				.getAllOrders()).withSelfRel());
	}
	
	@GetMapping("/customers/orders/{id}")
	Order getOrder(@PathVariable Long id) {
		Order order = customerOrderService.getOrderById(id);
		representationAssembler.addLinksToOrder(order);
		return order;
	}
	
	@GetMapping("/customers/{customerId}/orders")
	CollectionModel<Order> getOrdersForCustomer(@PathVariable Long customerId){
		List<Order> orders = customerOrderService.getAllOrdersForCustomer(customerId);
		representationAssembler.addLinksToOrders(orders);	
		return new CollectionModel<Order>(orders, linkTo(methodOn(OrderController.class)
				.getAllOrders()).withSelfRel());
	}
	
	@PostMapping("/customers/{customerId}/orders")
	@ResponseStatus(HttpStatus.CREATED)
	Order addOrderToCustomer(@PathVariable Long customerId, @RequestBody @Valid OrderForm orderForm) {
		Order order = customerOrderService.addOrderToCustomer(customerId, OrderStatus.IN_PROGRESS, orderForm.getPrice(),
				orderForm.getDescription());
		representationAssembler.addLinksToOrder(order);
		return order;
	}
	
	@PutMapping("/customers/orders/{orderId}/complete")
	Order completeOrder(@PathVariable Long orderId) {
		Order order = customerOrderService.updateOrderStatus(orderId, OrderStatus.COMPLETED);
		representationAssembler.addLinksToOrder(order);
		return order;
	}
	
	@PutMapping("/customers/orders/{orderId}/cancel")
	Order cancelOrder(@PathVariable Long orderId) {
		Order order = customerOrderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
		representationAssembler.addLinksToOrder(order);
		return order;
	}
	
	@DeleteMapping("/customers/orders/{orderId}/delete")
	ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
		customerOrderService.deleteOrder(orderId);
		return ResponseEntity.noContent().build();
	}

	
	
}
