package se.kth.id1212.currencyserv.currency.presentation.curr;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

/**
 * This class represents the HTML Form for updating conversion rates between
 * currencies.
 * 
 * @author Antonio
 *
 */
class ConversionRateForm {
	@NotNull(message = "Please enter a rate")
	@PositiveOrZero(message = "Rate must be positive and greater than zero")
	private Double rate;
	
	@NotBlank(message = "Please enter a currency")
	@Pattern(regexp = "^[\\p{L}\\p{M}*]*$", message = "Only letters are allowed")
	@Size(min = 3, max = 3, message = "Currency must be 3 letters")
	private String fromCurrency;
	
	@NotBlank(message = "Please enter a currency")
	@Pattern(regexp = "^[\\p{L}\\p{M}*]*$", message = "Only letters are allowed")
	@Size(min = 3, max = 3, message = "Currency must be 3 letters")
	private String toCurrency;
	
	/**
	 * @return the new rate.
	 */
	public double getRate() {
		return this.rate;
	}
	
	/**
	 * Sets the new rate.
	 * @param rate The new rate.
	 */
	public void setRate(double rate) {
		this.rate = rate;
	}
	
	/**
	 * @return the currency to convert from.
	 */
	public String getFromCurrency() {
		return this.fromCurrency;
	}
	
	/**
	 * Sets the currency to convert from.
	 * 
	 * @param currency the currency to convert from.
	 */
	public void setFromCurrency(String currency) {
		this.fromCurrency = currency.toUpperCase();
	}
	
	/**
	 * @return the currency to convert to.
	 */
	public String getToCurrency() {
		return this.toCurrency;
	}
	
	/**
	 * Sets the currency to convert to.
	 * 
	 * @param currency The currency to convert to.
	 */
	public void setToCurrency(String currency) {
		this.toCurrency = currency.toUpperCase();
	}
	
}
