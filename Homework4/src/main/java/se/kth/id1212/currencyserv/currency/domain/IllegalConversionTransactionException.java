package se.kth.id1212.currencyserv.currency.domain;

/**
 * Exception that is thrown when attempting to do a transaction
 * that is not allowed by the currency conversion rules. For example:
 * when a currency that does not exist is given, or if a negative number
 * is given.
 * 
 * @author Antonio
 *
 */
public class IllegalConversionTransactionException extends Exception{
	/**
	 * Creates an <code>IlleganConversionTransactionException</code> with
	 * the given message.
	 * 
	 * @param message the message, i.e why the exception was thrown.
	 */
	public IllegalConversionTransactionException(String message) {
		super(message);
	}
}
