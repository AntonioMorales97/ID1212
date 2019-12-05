package se.kth.id1212.currencyserv.currency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.kth.id1212.currencyserv.currency.domain.CurrencyConversion;

/**
 * Contains the database access for <code>CurrencyConversion</code>s.
 * 
 * @author Antonio
 *
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface ConversionRateRepository extends JpaRepository<CurrencyConversion, Long>{
	/**
	 * Tries to find the <code>CurrencyConversion</code> with the given
	 * currencies in the database and return it.
	 * 
	 * @param fromCurrency The currency to convert from.
	 * @param toCurrency The currency to convert to.
	 * @return the found <code>CurrencyConversion</code> if found;<code>null</code>
	 * otherwise.
	 */
	CurrencyConversion findByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);
}
