package se.kth.id1212.rest.presentation.error;

/**
 * Thrown whenever an <code>Order</code> is not found.
 * 
 * @author Antonio
 *
 */
public class OrderNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance of this exception whenever an <code>Order</code> was not found.
	 * 
	 * @param msg The message holding information why the exception was thrown.
	 */
	public OrderNotFoundException(String msg) {
		super(msg);
	}
}
