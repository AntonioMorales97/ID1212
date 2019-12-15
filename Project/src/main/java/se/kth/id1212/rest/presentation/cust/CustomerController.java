package se.kth.id1212.rest.presentation.cust;

import java.util.List;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import se.kth.id1212.rest.application.CustomerOrderService;
import se.kth.id1212.rest.domain.Customer;

/**
 * The REST API for the customers (<code>Customer</code>s). The REST API can list all the customers, retrieve specific customers
 * with a given id or personal number, create new customers, update customer membership, and delete customers.
 * 
 * @author Antonio
 *
 */
@RestController
@Validated
class CustomerController {

	@Autowired
	private CustomerOrderService customerOrderService;

	@Autowired
	private RepresentationAssembler representationAssembler;

	/**
	 * List all <code>Customers</code>s.
	 * 
	 * @return a <code>CollectionModel</code> with all the customers embedded.
	 */
	@GetMapping("/customers")
	CollectionModel<Customer> getAllCustomers(){
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
	Customer getCustomer(@PathVariable Long id) {
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
	Customer getCustomerByPersonalNumber(@RequestParam(name = "number", required = true) String personalNumber) {
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
	Customer addCustomer(@RequestBody @Valid Customer newCustomer) {
		Customer customer = customerOrderService.addCustomer(newCustomer);
		representationAssembler.addLinkToCustomer(customer);
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
	Customer updateCustomer(@RequestBody @Valid CustomerUpdateForm customerUpdateForm, @PathVariable Long id) {
		Customer customer = customerOrderService.updateCustomerMembership(id, customerUpdateForm.getMembership());
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
	ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
		customerOrderService.deleteCustomer(id);
		return ResponseEntity.noContent().build();
	}

}
