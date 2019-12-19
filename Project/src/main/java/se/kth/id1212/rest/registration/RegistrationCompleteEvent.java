package se.kth.id1212.rest.registration;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import se.kth.id1212.rest.domain.Customer;

public class RegistrationCompleteEvent extends ApplicationEvent {
	
	private static final long serialVersionUID = 1L;

	private Customer customer;
	private Locale loc;
	private String url;
	
	public RegistrationCompleteEvent(Customer customer, Locale locale, String url) {
		super(customer);
		this.customer = customer;
		this.loc = locale;
		this.url = url;
	}
	
	public Customer getCustomer() {
		return this.customer;
	}
	
	public Locale getLocale() {
		return this.loc;
	}
	
	public String getUrl() {
		return this.url;
	}
}

