package se.kth.id1212.rest.application;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.kth.id1212.rest.application.exception.CustomerNotFoundException;
import se.kth.id1212.rest.application.exception.IllegalTransactionException;
import se.kth.id1212.rest.application.exception.InvalidCredentialsException;
import se.kth.id1212.rest.application.exception.VerificationTokenNotFoundException;
import se.kth.id1212.rest.domain.Customer;
import se.kth.id1212.rest.domain.VerificationToken;
import se.kth.id1212.rest.presentation.dto.CustomerDTO;
import se.kth.id1212.rest.repository.CustomerRepository;
import se.kth.id1212.rest.repository.VerificationTokenRepository;

/**
 * Implements <code>ICustomerServices</code> with the services related to <code>Customer</code>s.
 * 
 * @author Antonio
 *
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
public class CustomerService implements ICustomerService{

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private VerificationTokenRepository verificationTokenRepo;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(13);

	@Override
	public List<Customer> getAllCustomers(){
		return this.customerRepo.findAll();
	}

	@Override
	public Customer getCustomerById(Long id) {
		return findCustomerById(id);
	}

	@Override
	public Customer getCustomerByPersonalNumber(String personalNumber) {
		Customer customer = this.customerRepo.findByPersonalNumber(personalNumber);
		if(customer != null)
			return customer;

		throw new CustomerNotFoundException("The customer with the personal number: " + personalNumber + " was not found!");
	}

	@Override
	public Customer addCustomer(CustomerDTO customerDto) {
		if(personalNumberExists(customerDto.getPersonalNumber()))
			throw new IllegalTransactionException("A customer with the given personal number already exists!");

		if(emailExists(customerDto.getEmail()))
			throw new IllegalTransactionException("A customer with the given email already exists!");

		Customer newCustomer = new Customer(customerDto.getFirstName(), customerDto.getLastName(), customerDto.getPersonalNumber(),
				customerDto.getAge(), customerDto.getMembership());
		newCustomer.setEmail(customerDto.getEmail());
		newCustomer.setPassword(passwordEncoder.encode(customerDto.getPassword()));
		this.customerRepo.save(newCustomer);
		return newCustomer;
	}

	@Override
	public Customer updateCustomerMembership(Long id, String membership) {
		Customer customer = findCustomerById(id);
		customer.setMembership(membership);
		return customer;
	}

	@Override
	public Customer loginCustomer(String email, String password) {
		Customer customer = customerRepo.findByEmail(email);
		if(customer == null)
			throw new CustomerNotFoundException("No customer with the email: " + email + " was found!");

		if(passwordEncoder.matches(password, customer.getPassword())) {
			verificationTokenRepo.deleteByCustomer(customer);
			customer.setVerified(true);
			return customer;
		}
		throw new InvalidCredentialsException("Could not login customer because of invalid credentials!");
	}

	@Override
	public Customer confirmCustomerEmailWithVerificationToken(String token) {
		VerificationToken verToken = verificationTokenRepo.findByToken(token);
		if(verToken == null)
			throw new VerificationTokenNotFoundException("Verification token was not found!");

		Customer customer = verToken.getCustomer();
		if(customer.getVerified() == true)
			throw new IllegalTransactionException("Customer is already verified!");

		verificationTokenRepo.delete(verToken);
		customer.setVerified(true);
		return customer;
	}

	@Override
	public void deleteCustomer(Long id) {
		Customer customer = findCustomerById(id);
		if(customer.isRemovable()) {
			this.customerRepo.deleteById(id);
			return;
		}
		throw new IllegalTransactionException("Cannot delete a customer with orders that are in progress!");
	}

	@Override
	public void createVerificationTokenForCustomer(Customer customer, String token) {
		VerificationToken verToken = new VerificationToken(token, customer);
		this.verificationTokenRepo.save(verToken);
	}

	private Customer findCustomerById(Long id) {
		Optional<Customer> customer = this.customerRepo.findById(id);
		if(customer.isPresent())
			return customer.get();

		throw new CustomerNotFoundException("The customer with the id: " + id + ", was not found!");	
	}

	private boolean personalNumberExists(String personalNumber) {
		return this.customerRepo.findByPersonalNumber(personalNumber) != null ? true : false;
	}

	private boolean emailExists(String email) {
		return this.customerRepo.findByEmail(email) != null ? true : false;
	}
}
