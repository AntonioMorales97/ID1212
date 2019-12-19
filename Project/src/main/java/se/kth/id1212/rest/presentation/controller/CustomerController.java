package se.kth.id1212.rest.presentation.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import se.kth.id1212.rest.application.ICustomerService;
import se.kth.id1212.rest.domain.Customer;
import se.kth.id1212.rest.presentation.dto.CustomerDTO;
import se.kth.id1212.rest.presentation.form.CustomerLoginForm;
import se.kth.id1212.rest.presentation.form.CustomerUpdateForm;
import se.kth.id1212.rest.presentation.util.RepresentationAssembler;
import se.kth.id1212.rest.registration.RegistrationCompleteEvent;

/**
 * The REST API for the customers (<code>Customer</code>s). The REST API can list all the customers, retrieve specific customers
 * with a given id or personal number, create new customers, confirm new customers, update customer's membership, and delete customers.
 * 
 * @author Antonio
 *
 */
@RestController
@Validated
public class CustomerController {

	@Autowired
	private ICustomerService customerOrderService;

	@Autowired
	private RepresentationAssembler representationAssembler;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	/**
	 * List all <code>Customers</code>s.
	 * 
	 * @return a <code>CollectionModel</code> with all the customers embedded.
	 */
	@GetMapping("/customers")
	public CollectionModel<Customer> getAllCustomers(){
		List<Customer> customers = customerOrderService.getAllCustomers();
		representationAssembler.addLinksToCustomers(customers);

		return new CollectionModel<Customer>(customers, linkTo(methodOn(CustomerController.class)
				.getAllCustomers()).withSelfRel());
	}

	/**
	 * Retrieves a <code>Customer</code> with a specific id.
	 * 
	 * @param id The ID of the <code>Customer</code>.
	 * @return the <code>Customer</code>.
	 */
	@GetMapping("/customers/{id}")
	public Customer getCustomer(@PathVariable Long id) {
		Customer customer = customerOrderService.getCustomerById(id);
		representationAssembler.addLinkToCustomer(customer);
		return customer;
	}

	/**
	 * Retrieves a <code>Customer</code> with a personal number.
	 * 
	 * @param personalNumber The personal number of the <code>Customer</code>.
	 * @return the <code>Customer</code>.
	 */
	@GetMapping("/customers/findByPersonalNumber")
	public Customer getCustomerByPersonalNumber(@RequestParam(name = "number", required = true) String personalNumber) {
		Customer customer = customerOrderService.getCustomerByPersonalNumber(personalNumber);
		representationAssembler.addLinkToCustomer(customer);
		return customer;
	}

	/**
	 * Creates a new <code>Customer</code>.
	 * 
	 * @param newCustomer The new <code>Customer</code> to be created and stored.
	 * @return the created <code>Customer</code>.
	 */
	@PostMapping("/customers")
	@ResponseStatus(HttpStatus.CREATED)
	public Customer addCustomer(@RequestBody @Valid CustomerDTO customerDto, HttpServletRequest request) {
		Customer customer = customerOrderService.addCustomer(customerDto);
		representationAssembler.addLinkToCustomer(customer);
		eventPublisher.publishEvent(new RegistrationCompleteEvent(customer, request.getLocale(), getUrl(request)));
		return customer;
	}

	/**
	 * Updates a <code>Customer</code>'s membership. The possible memberships can be seen at
	 * {@link CustomerMembership}.
	 * 
	 * @param customerUpdateForm The update form to set the new membership.
	 * @param id The ID of the <code>Customer</code>.
	 * @return the updated <code>Customer</code>.
	 */
	@PutMapping("/customers/{id}")
	public Customer updateCustomer(@RequestBody @Valid CustomerUpdateForm customerUpdateForm, @PathVariable Long id) {
		Customer customer = customerOrderService.updateCustomerMembership(id, customerUpdateForm.getMembership());
		representationAssembler.addLinkToCustomer(customer);
		return customer;
	}

	/**
	 * Used to login a <code>Customer</code> by email and password.
	 * OBS: Login is not implemented. One could insert an HttpOnly Cookie
	 * with JWT token to authenticate a customer. For simplicity, login is only
	 * simulated.
	 * 
	 * @param confirmForm A <code>CustomerLoginForm</code> holding the email and password.
	 * @return the verified/confirmed/logged in <code>Customer</code>.
	 */
	@PostMapping("/customers/login")
	public Customer loginCustomer(@RequestBody @Valid CustomerLoginForm loginForm) {
		Customer customer = customerOrderService.loginCustomer(loginForm.getEmail(),
				loginForm.getPassword());
		representationAssembler.addLinkToCustomer(customer);
		return customer;
	}

	/**
	 * Used to verify a <code>Customer</code> with the given token.
	 * 
	 * @param token The token. See {@link VerificationToken}.
	 * @return the verified/confirmed <code>Customer</code>.
	 */
	@GetMapping("/customers/confirm")
	public Customer confirmEmailWithToken(@RequestParam(name = "token", required = true) String token) {
		Customer customer = customerOrderService.confirmCustomerEmailWithVerificationToken(token);
		representationAssembler.addLinkToCustomer(customer);
		return customer;
	}

	/**
	 * Deletes an <code>Customer</code>.
	 * 
	 * @param id The ID of the <code>Customer</code>.
	 * @return an empty <code>ResponseEntity</code> with HTTP 204 if successful.
	 */
	@DeleteMapping("/customers/{id}")
	public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
		customerOrderService.deleteCustomer(id);
		return ResponseEntity.noContent().build();
	}

	private String getUrl(HttpServletRequest request) {
		return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
	}
}
