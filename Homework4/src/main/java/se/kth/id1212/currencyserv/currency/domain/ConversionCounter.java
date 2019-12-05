package se.kth.id1212.currencyserv.currency.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Class representing the number of conversions made since the start of the 
 * application. The number of conversions are stored in the database.
 * 
 * @author Antonio
 *
 */
@Entity
@Table(name = "conversion_counter")
public class ConversionCounter implements ConversionCounterDTO{
	private static final String SEQ_NAME_KEY = "SEQ_NAME";
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME_KEY)
	@SequenceGenerator(name = SEQ_NAME_KEY, sequenceName = "CURRENCY_SEQUENCE")
	@Column(name = "id")
	private long id;
	
	@Column(name = "count")
	private long count;
	
	/**
	 * Used by JPA (don't use!)
	 */
	protected ConversionCounter() {
	}
	
	/**
	 * @return the number of conversions made since the start of the
	 * application.
	 */
	@Override
	public long getCount() {
		return this.count;
	}
	
	/**
	 * Increments the number of conversions by one.
	 */
	public void increment() {
		this.count++;
	}
}
