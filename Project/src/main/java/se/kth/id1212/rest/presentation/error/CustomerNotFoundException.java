package se.kth.id1212.rest.presentation.error;

/**
 * Thrown whenever a <code>Customer</code> is not found.
 * 
 * @author Antonio
 *
 */
public class CustomerNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance of this exception whenever a <code>Customer</code> was not found.
	 * 
	 * @param msg The message holding information why the exception was thrown.
	 */
	public CustomerNotFoundException(String msg) {
		super(msg);
	}
}
