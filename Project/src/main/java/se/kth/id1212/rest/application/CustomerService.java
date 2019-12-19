package se.kth.id1212.rest.application;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.kth.id1212.rest.domain.Customer;
import se.kth.id1212.rest.domain.VerificationToken;
import se.kth.id1212.rest.presentation.dto.CustomerDTO;
import se.kth.id1212.rest.presentation.error.CustomerNotFoundException;
import se.kth.id1212.rest.presentation.error.IllegalTransactionException;
import se.kth.id1212.rest.repository.CustomerRepository;
import se.kth.id1212.rest.repository.VerificationTokenRepository;

/**
 * Holds all the possible services for a client in this application and
 * passes it down to the domain and repository layers.
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

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

	/**
	 * @return all <code>Customer</code>s stored in the database.
	 */
	@Override
	public List<Customer> getAllCustomers(){
		return this.customerRepo.findAll();
	}

	/**
	 * Tries to find and get the <code>Customer</code> with the given ID.
	 *  
	 * @param id The ID of the customer.
	 * @return the <code>Customer</code> if found.
	 */
	@Override
	public Customer getCustomerById(Long id) {
		return findCustomerById(id);
	}

	/**
	 * Tries to find and get the <code>Customer</code> with the given personal number.
	 * 
	 * @param personalNumber The personal number of the customer.
	 * @return the <code>Customer</code> if found.
	 */
	@Override
	public Customer getCustomerByPersonalNumber(String personalNumber) {
		Customer customer = this.customerRepo.findByPersonalNumber(personalNumber);
		if(customer != null)
			return customer;

		throw new CustomerNotFoundException("The customer with the personal number: " + personalNumber + " was not found!");
	}

	/**
	 * Tries to add a new <code>Customer</code>. Also creates a <code>VerificationToken</code>
	 * to verify the created <code>Customer</code>. This can be used when confirming that 
	 * the email is valid by sending the token to the given email. But, for simplicity, 
	 * no email is sent. The token is stored in the database.
	 * 
	 * @param newCustomer The new <code>Customer</code> to be added.
	 * @return the added <code>Customer</code>.
	 */
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

	/**
	 * Tries to update the membership of an existing <code>Customer</code>.
	 * 
	 * @param id The ID of the <code>Customer</code>.
	 * @param membership The new membership of the <code>Customer</code>.
	 * @return the updated <code>>Customer</code>.
	 */
	@Override
	public Customer updateCustomerMembership(Long id, String membership) {
		Customer customer = findCustomerById(id);
		customer.setMembership(membership);
		return customer;
	}

	/**
	 * Login a <code>Customer</code> by matching the stored password with the given
	 * password for the given email. Uses the <code>BCryptPasswordEncoder</code>.
	 * OBS: Does not really login a customer. For simplicity, if email and password
	 * are correct, just return the <code>Customer</code>.
	 * 
	 * @param email The email of the <code>Customer</code> to be verified.
	 * @param password The given password of the <code>Customer</code>.
	 * @return the "logged in" <code>Customer</code> if successful.
	 */
	@Override
	public Customer loginCustomer(String email, String password) {
		Customer customer = customerRepo.findByEmail(email);
		if(customer == null)
			throw new CustomerNotFoundException("No customer with the email: " + email + " was found!");
		if(customer.getVerified() == true)
			throw new IllegalTransactionException("Customer is already verified!");
		if(passwordEncoder.matches(password, customer.getPassword())) {
			verificationTokenRepo.deleteByCustomer(customer);
			customer.setVerified(true);
			return customer;
		}
		throw new IllegalTransactionException("Invalid credentials!"); //change exception maybe...
	}

	/**
	 * Confirms a <code>Customer</code> with a given token that is stored as a 
	 * <code>VerificationToken</code>. Used to confirm customer email.
	 * 
	 * @param token The token.
	 * @return the verified <code>Customer</code> if successful.
	 */
	@Override
	public Customer confirmCustomerEmailWithVerificationToken(String token) {
		VerificationToken verToken = verificationTokenRepo.findByToken(token);
		if(verToken == null) {
			throw new IllegalTransactionException("Token was not found"); //change exception
		}
		Customer customer = verToken.getCustomer();
		if(customer.getVerified() == true)
			throw new IllegalTransactionException("Customer is already verified!");

		verificationTokenRepo.delete(verToken);
		customer.setVerified(true);
		return customer;
	}

	/**
	 * Tries to delete a <code>Customer</code> with the given ID.
	 * 
	 * @param id The ID of the <code>Customer</code>.
	 * @return the deleted <code>Customer</code> if found and deleted.
	 */
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
