package se.kth.id1212.rest.application;

import java.util.List;

import se.kth.id1212.rest.domain.Order;
import se.kth.id1212.rest.enums.OrderStatus;

public interface IOrderService {
	public List<Order> getAllOrders();
	
	public List<Order> getAllOrdersForCustomer(Long id);
	
	public Order getOrderById(Long id);
	
	public Order addOrderToCustomer(Long id, OrderStatus status, Double price, String description);
	
	public Order updateOrderStatus(Long id, OrderStatus status);
	
	public void deleteOrder(Long id);
}
