package se.kth.id1212.rest.application.exception;

/**
 * Thrown whenever a <code>VerificationToken</code> is not found.
 * 
 * @author Antonio
 *
 */
public class VerificationTokenNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance of this exception whenever a <code>VerificationToken</code> was not found.
	 * 
	 * @param msg The message holding information why the exception was thrown.
	 */
	public VerificationTokenNotFoundException(String msg) {
		super(msg);
	}
}
