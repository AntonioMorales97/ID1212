package se.kth.id1212.rest.application.exception;

/**
 * Thrown whenever an illegal transaction is made in this application.
 * 
 * @author Antonio
 *
 */
public class IllegalTransactionException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance of this exception whenever an illegal transaction is made in this application.
	 * 
	 * @param msg The message holding information why the exception was thrown.
	 */
	public IllegalTransactionException(String msg) {
		super(msg);
	}
}
