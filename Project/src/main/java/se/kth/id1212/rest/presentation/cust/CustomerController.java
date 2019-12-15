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

@RestController
@Validated
class CustomerController {
	
	@Autowired
	private CustomerOrderService customerOrderService;
	
	@Autowired
	private RepresentationAssembler representationAssembler;
	
	/*
	@GetMapping("/customers")
	CollectionModel<EntityModel<Customer>> getAllCustomers(){
		List<EntityModel<Customer>> customers = customerOrderService.getAllCustomers()
				.stream().map(customer -> {
					return customerResourceAssembler.toModel(customer);
				}).collect(Collectors.toList());
		return new CollectionModel<>(customers, linkTo(methodOn(CustomerController.class)
				.getAllCustomers()).withSelfRel());
	}
	*/
	
	@GetMapping("/customers")
	CollectionModel<Customer> getAllCustomers(){
		List<Customer> customers = customerOrderService.getAllCustomers();
		representationAssembler.addLinksToCustomers(customers);
		
		return new CollectionModel<Customer>(customers, linkTo(methodOn(CustomerController.class)
				.getAllCustomers()).withSelfRel());
	}
	
	@GetMapping("/customers/{id}")
	Customer getCustomer(@PathVariable Long id) {
		Customer customer = customerOrderService.getCustomerById(id);
		representationAssembler.addLinkToCustomer(customer);
		return customer;
	}
	
	@GetMapping("/customers/findByPersonalNumber")
	Customer getCustomerByPersonalNumber(@RequestParam(name = "number", required = true) String personalNumber) {
		Customer customer = customerOrderService.getCustomerByPersonalNumber(personalNumber);
		representationAssembler.addLinkToCustomer(customer);
		return customer;
	}
	
	@PostMapping("/customers")
	@ResponseStatus(HttpStatus.CREATED)
	Customer addCustomer(@RequestBody @Valid Customer newCustomer) {
		Customer customer = customerOrderService.addCustomer(newCustomer);
		representationAssembler.addLinkToCustomer(customer);
		return customer;
	}
	
	@PutMapping("/customers/{id}")
	Customer updateCustomer(@RequestBody @Valid CustomerUpdateForm customerUpdateForm, @PathVariable Long id) {
		Customer customer = customerOrderService.updateCustomerMembership(id, customerUpdateForm.getMembership());
		representationAssembler.addLinkToCustomer(customer);
		return customer;
	}

	/**
	 * Builds the response entity with no body.
	 * @param id The id of a <code>Customer</code>.
	 * @return HTTP 204 if successful.
	 */
	@DeleteMapping("/customers/{id}")
	ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
		customerOrderService.deleteCustomer(id);
		return ResponseEntity.noContent().build();
	}

}
