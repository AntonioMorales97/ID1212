package se.kth.id1212.currencyserv.currency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.kth.id1212.currencyserv.currency.domain.ConversionCounter;

/**
 * Contains the database access for the <code>ConversionCounter</code>.
 * 
 * @author Antonio
 *
 */
@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface ConversionCounterRepository extends JpaRepository<ConversionCounter, Long>{
	/**
	 * Tries to find the <code>ConversionCounter</code> stored in the 
	 * database with the given ID and return it.
	 * 
	 * @param id The ID of the counter.
	 * @return the <code>ConversionCounter</code> if found; <code>null</code>
	 * otherwise.
	 */
	ConversionCounter findById(long id);
}
