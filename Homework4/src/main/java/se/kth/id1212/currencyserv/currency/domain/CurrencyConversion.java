package se.kth.id1212.currencyserv.currency.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Class representing conversions between two currencies that exists
 * in the database.
 * 
 * @author Antonio
 *
 */
@Entity
@Table(name = "currency_conversion")
public class CurrencyConversion {
	private static final String SEQ_NAME_KEY = "SEQ_NAME";
	
	/**
	 * Required by JPA (don't use!)
	 */
	protected CurrencyConversion() {
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME_KEY)
	@SequenceGenerator(name = SEQ_NAME_KEY, sequenceName = "CURRENCY_SEQUENCE")
	@Column(name = "id")
	private long id;
	
	@NotNull(message = "{conversion.rate.missing}")
	@Column(name = "rate")
	private double rate;
	
	@NotNull(message = "{conversion.fromcurrency.missing}")
	@Column(name = "from_currency")
	private String fromCurrency;
	
	@NotNull(message = "{conversion.tocurrency.missing}")
	@Column(name = "to_currency")
	private String toCurrency;
	
	/**
	 * Updates the conversion rate for this conversion.
	 * 
	 * @param rate the new conversion rate.
	 */
	public void setRate(double rate) {
		this.rate = rate;
	}
	
	/**
	 * Updates the rate of the inverse conversion. E.g EUR -> SEK = 10, the inverse is
	 * therefore: SEK -> EUR = 1/10.
	 * 
	 * @param rate the new conversion rate for the inversion conversion.
	 */
	public void setInverseRate(double rate) {
		this.rate = rate == 0 ? 0 : 1/rate;
	}

	/**
	 * Converts the given amount for this conversion.
	 * 
	 * @param amount The given amount to convert.
	 * @return the new converted amount.
	 * @throws IllegalConversionTransactionException If attempting to convert a negative amount.
	 */
	public double convert(double amount) throws IllegalConversionTransactionException {
		if(amount < 0) {
			throw new IllegalConversionTransactionException("Attempt to convert negative amount: " + amount);
		}
		return this.rate * amount;
	}
	
	/**
	 * Converts the given amount for this inverse conversion. E.g EUR -> SEK = 10, the inverse is
	 * therefore: SEK -> EUR = 1/10.
	 * 
	 * @param amount The amount to convert.
	 * @return the new converted amount.
	 * @throws IllegalConversionTransactionException if attempting to convert a negative amount.
	 */
	public double inverseConvert(double amount) throws IllegalConversionTransactionException {
		if(amount < 0) {
			throw new IllegalConversionTransactionException("Attempt to convert negative amount: " + amount);
		}
		return this.rate == 0 ? 0 : ((1/this.rate) * amount);
	}
	
	

}
