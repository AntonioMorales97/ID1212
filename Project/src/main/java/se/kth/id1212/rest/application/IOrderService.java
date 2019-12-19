package se.kth.id1212.rest.application;

import java.util.List;

import se.kth.id1212.rest.domain.Order;
import se.kth.id1212.rest.enums.OrderStatus;

/**
 * An interface for the services related to <code>Order</code>s.
 * 
 * @author Antonio
 *
 */
public interface IOrderService {

	/**
	 * @return all the <code>Order</code>s stored in the database.
	 */
	public List<Order> getAllOrders();

	/**
	 * @param id The ID of a <code>Customer</code>.
	 * @return all the <code>Order</code>s of the customer.
	 */
	public List<Order> getAllOrdersForCustomer(Long id);

	/**
	 * @param id The ID of an <code>Order</code>.
	 * @return the <code>Order</code>.
	 */
	public Order getOrderById(Long id);

	/**
	 * Add a new <code>Order</code> for a <code>Customer</code>.
	 * 
	 * @param id The ID of the <code>Customer</code>.
	 * @param status The status of the <code>Order</code>.
	 * @param price The price of the <code>Order</code>.
	 * @param description The <code>Order</code> description.
	 * @return the created <code>Order</code>.
	 */
	public Order addOrderToCustomer(Long id, OrderStatus status, Double price, String description);

	/**
	 * Updates the status of an <code>Order</code>.
	 * 
	 * @param id The ID of the <code>Order</code>.
	 * @param status The new <code>OrderStatus</code> to be updated to.
	 * @return the updated <code>Order</code>.
	 */
	public Order updateOrderStatus(Long id, OrderStatus status);

	/**
	 * Deletes an <code>Order</code> with the given ID.
	 * 
	 * @param id The ID of the <code>Order</code> to be deleted.
	 */
	public void deleteOrder(Long id);
}
