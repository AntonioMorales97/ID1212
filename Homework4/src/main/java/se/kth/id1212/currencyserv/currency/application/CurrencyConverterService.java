package se.kth.id1212.currencyserv.currency.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.kth.id1212.currencyserv.currency.domain.CurrencyConversion;
import se.kth.id1212.currencyserv.currency.domain.ConversionCounter;
import se.kth.id1212.currencyserv.currency.domain.ConversionCounterDTO;
import se.kth.id1212.currencyserv.currency.domain.IllegalConversionTransactionException;
import se.kth.id1212.currencyserv.currency.repository.ConversionRateRepository;
import se.kth.id1212.currencyserv.currency.repository.ConversionCounterRepository;

/**
 * Represents the currency converter application and holds the possible client actions
 * and calls to the necessary methods in the domain layer.
 * 
 * The class has transaction management by the <code>Transactional</code> annotation,
 * meaning that a method call will begin a transaction and end the transaction when the 
 * method call returns. A transaction will be committed when everything is successful or
 * rolled back if something goes wrong.
 * 
 * @author Antonio
 *
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
public class CurrencyConverterService {
	@Autowired
	private ConversionRateRepository conversionRepo;
	@Autowired
	private ConversionCounterRepository counterRepo;
	
	private static final long COUNTER_ID = 1;
	
	/**
	 * Gets the counter from the <code>ConversionCounterRepository</code> as a
	 * <code>ConversionCounterDTO</code>. 
	 * 
	 * @return the counter.
	 */
	public ConversionCounterDTO getCounter() {
		ConversionCounterDTO count = counterRepo.findById(COUNTER_ID);
		return count;
	}
	
	/**
	 * Converts the given amount from one given currency to another.
	 * 
	 * @param fromCurrency The currency to convert from.
	 * @param toCurrency The currency to convert to.
	 * @param amount The amount to convert.
	 * @return the converted amount.
	 * @throws IllegalConversionTransactionException If the conversion could not be made.
	 */
	public double convertAmount(String fromCurrency, String toCurrency, double amount) throws IllegalConversionTransactionException {
		if(fromCurrency.equals(toCurrency))
			return amount;
		CurrencyConversion conversion = conversionRepo.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency);
		if(conversion == null) {
			conversion = conversionRepo.findByFromCurrencyAndToCurrency(toCurrency, fromCurrency);
			if(conversion == null)
				throw new IllegalConversionTransactionException("Conversion from " + fromCurrency + " to " + toCurrency + " does not exist.");
			return conversion.inverseConvert(amount);
		}
		return conversion.convert(amount);
	}
	
	/**
	 * Sets the conversion rate between two existing currencies.
	 * 
	 * @param fromCurrency The currency to convert from.
	 * @param toCurrency The currency to convert to.
	 * @param rate The new conversion rate.
	 * @throws IllegalConversionTransactionException If the conversion rate could not be set.
	 */
	public void setConversionRate(String fromCurrency, String toCurrency, double rate) throws IllegalConversionTransactionException {
		if(fromCurrency.equals(toCurrency)) 
			return;
		CurrencyConversion conversion = conversionRepo.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency);
		if(conversion == null) {
			conversion = conversionRepo.findByFromCurrencyAndToCurrency(toCurrency, fromCurrency);
			if(conversion == null)
				throw new IllegalConversionTransactionException("Conversion from " + fromCurrency + " to " + toCurrency + " does not exist.");
			conversion.setInverseRate(rate);
		} else {
			conversion.setRate(rate);
		}
		conversionRepo.save(conversion);
	}

	/**
	 * Increment the conversion counter by one.
	 */
	public void incrementConversionCounter() {
		ConversionCounter counter = counterRepo.findById(COUNTER_ID);
		if(counter != null) {
			counter.increment();
			counterRepo.save(counter);
		} else {
			System.out.println("Counter is null :(");
		}
	}
	
}
