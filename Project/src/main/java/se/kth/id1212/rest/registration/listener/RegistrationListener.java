package se.kth.id1212.rest.registration.listener;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import se.kth.id1212.rest.application.ICustomerService;
import se.kth.id1212.rest.domain.Customer;
import se.kth.id1212.rest.registration.RegistrationCompleteEvent;

/**
 * An <code>ApplicationListener</code> to listen for <code>RegistrationCompleteEvent</code>s. When
 * a registration has completed an email is sent for confirmation.
 * 
 * @author Antonio
 *
 */
@Component
public class RegistrationListener implements ApplicationListener<RegistrationCompleteEvent>{

	@Autowired
	private ICustomerService customerService;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	Environment env;


	@Override
	public void onApplicationEvent(RegistrationCompleteEvent event) {
		this.sendConfirmEmail(event);
	}

	private void sendConfirmEmail(RegistrationCompleteEvent event) {
		Customer customer = event.getCustomer();
		String token = UUID.randomUUID().toString();
		customerService.createVerificationTokenForCustomer(customer, token);

		mailSender.send(buildEmailMessage(event, customer, token));
	}

	private SimpleMailMessage buildEmailMessage(RegistrationCompleteEvent event, Customer customer, String token) {
		String customerEmail = customer.getEmail();
		String subject = "Email Confirmation";
		String confirmUrl = event.getUrl() + "/customer/confirm?token=" + token;
		String message = "Hello customer\n\nYou have been added. Please confirm by following the url :)";
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(customerEmail);
		email.setSubject(subject);
		email.setText(message + "\n\n" + confirmUrl);
		email.setFrom(env.getProperty("support.email"));
		return email;
	}

}
