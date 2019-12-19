package se.kth.id1212.rest.registration;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import se.kth.id1212.rest.domain.Customer;

/**
 * An <code>ApplicationEvent</code> for completed registrations.
 * 
 * @author Antonio
 *
 */
public class RegistrationCompleteEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	private Customer customer;
	private Locale loc;
	private String url;

	/**
	 * Creates a <code>RegistrationCompleteEvent</code>.
	 * 
	 * @param customer The <code>Customer</code> that was registreted.
	 * @param locale The <code>Locale</code>.
	 * @param url The URL for later confirmation.
	 */
	public RegistrationCompleteEvent(Customer customer, Locale locale, String url) {
		super(customer);
		this.customer = customer;
		this.loc = locale;
		this.url = url;
	}

	/**
	 * @return the <code>Customer</code>.
	 */
	public Customer getCustomer() {
		return this.customer;
	}

	/**
	 * @return the <code>Locale</code>.
	 */
	public Locale getLocale() {
		return this.loc;
	}

	/**
	 * @return the URL.
	 */
	public String getUrl() {
		return this.url;
	}
}

