package se.kth.id1212.rest.application;

import java.util.List;

import se.kth.id1212.rest.domain.Customer;
import se.kth.id1212.rest.presentation.dto.CustomerDTO;

public interface ICustomerService {
	public List<Customer> getAllCustomers();
	
	public Customer getCustomerById(Long id);
	
	public Customer getCustomerByPersonalNumber(String personalNumber);
	
	public Customer addCustomer(CustomerDTO customerDto);
	
	public Customer updateCustomerMembership(Long id, String membership);
	
	public Customer loginCustomer(String email, String password);
	
	public Customer confirmCustomerEmailWithVerificationToken(String token);
	
	public void deleteCustomer(Long id);
	
	public void createVerificationTokenForCustomer(Customer customer, String token);
}
