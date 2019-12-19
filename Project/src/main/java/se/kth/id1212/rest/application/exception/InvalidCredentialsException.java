package se.kth.id1212.rest.application.exception;

/**
 * Thrown whenever there are invalid credentials in a transaction.
 * 
 * @author Antonio
 *
 */
public class InvalidCredentialsException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance of <code>InvalidCredentialsException</code>.
	 * 
	 * @param msg The message holding information why the exception was thrown.
	 */
	public InvalidCredentialsException (String msg) {
		super(msg);
	}

}
