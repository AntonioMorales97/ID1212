package se.kth.id1212.currencyserv.currency.presentation.curr;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

/**
 * This class represents the HTML Form for currency conversions.
 * For simplicity, it also holds the converted amount after a conversion,
 * which is not actually part of the form.
 * 
 * @author Antonio
 *
 */
class CurrencyConversionForm {
	@NotNull(message = "Please enter an amount")
	@PositiveOrZero(message = "Amount must be positive and greater than zero")
	private Double amount;
	
	@NotBlank(message = "Please enter a currency")
	@Pattern(regexp = "^[\\p{L}\\p{M}*]*$", message = "Only letters are allowed")
	@Size(min = 3, max = 3, message = "Currency must be 3 letters")
	private String fromCurrency;
	
	@NotBlank(message = "Please enter a currency")
	@Pattern(regexp = "^[\\p{L}\\p{M}*]*$", message = "Only letters are allowed")
	@Size(min = 3, max = 3, message = "Currency must be 3 letters")
	private String toCurrency;
	
	private double converted;
	
	/**
	 * @return the amount to convert.
	 */
	public double getAmount() {
		return this.amount;
	}
	
	/**
	 * Sets the amount to convert.
	 * @param amount The amount.
	 */
	public void setAmount(double amount) {
		this.amount = amount;
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
	
	/**
	 * Sets the amount that was converted.
	 * 
	 * @param amount the amount after conversion.
	 */
	public void setConvertedAmount(double amount) {
		this.converted = amount;
	}
	
	/**
	 * @return the converted amount.
	 */
	public double getConvertedAmount() {
		return this.converted;
	}
	
}
