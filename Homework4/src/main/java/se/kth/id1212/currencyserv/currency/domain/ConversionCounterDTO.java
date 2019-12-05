package se.kth.id1212.currencyserv.currency.domain;

/**
 * Defines the possible method calls that can be made to a {@link ConversionCounter}
 * outside the application layer and the domain layer.
 * 
 * @author Antonio
 *
 */
public interface ConversionCounterDTO {
	/**
	 * @return the number of conversions made since the start of
	 * the application.
	 */
	public long getCount();
}
