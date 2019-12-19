package se.kth.id1212.rest.application;

import java.util.List;

import se.kth.id1212.rest.domain.Customer;
import se.kth.id1212.rest.presentation.dto.CustomerDTO;

public interface ICustomerService {

	/**
	 * @return all <code>Customer</code>s stored in the database.
	 */
	public List<Customer> getAllCustomers();

	/**
	 * Tries to find and get the <code>Customer</code> with the given ID.
	 *  
	 * @param id The ID of the customer.
	 * @return the <code>Customer</code> if found.
	 */
	public Customer getCustomerById(Long id);

	/**
	 * Tries to find and get the <code>Customer</code> with the given personal number.
	 * 
	 * @param personalNumber The personal number of the customer.
	 * @return the <code>Customer</code> if found.
	 */
	public Customer getCustomerByPersonalNumber(String personalNumber);

	/**
	 * Tries to add a new <code>Customer</code>. Also creates a <code>VerificationToken</code>
	 * to verify the created <code>Customer</code>. This can be used when confirming that 
	 * the email is valid by sending the token to the given email. But, for simplicity, 
	 * no email is sent. The token is stored in the database.
	 * 
	 * @param newCustomer The new <code>Customer</code> to be added.
	 * @return the added <code>Customer</code>.
	 */
	public Customer addCustomer(CustomerDTO customerDto);

	/**
	 * Tries to update the membership of an existing <code>Customer</code>.
	 * 
	 * @param id The ID of the <code>Customer</code>.
	 * @param membership The new membership of the <code>Customer</code>.
	 * @return the updated <code>>Customer</code>.
	 */
	public Customer updateCustomerMembership(Long id, String membership);

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
	public Customer loginCustomer(String email, String password);

	/**
	 * Confirms a <code>Customer</code> with a given token that is stored as a 
	 * <code>VerificationToken</code>. Used to confirm customer email.
	 * 
	 * @param token The token.
	 * @return the verified <code>Customer</code> if successful.
	 */
	public Customer confirmCustomerEmailWithVerificationToken(String token);

	/**
	 * Tries to delete a <code>Customer</code> with the given ID.
	 * 
	 * @param id The ID of the <code>Customer</code>.
	 * @return the deleted <code>Customer</code> if found and deleted.
	 */
	public void deleteCustomer(Long id);

	/**
	 * Creates a <code>VerificationToken</code> for the given <code>Customer</code>
	 * and with the created token.
	 * 
	 * @param customer The <code>Customer</code>.
	 * @param token The created token.
	 */
	public void createVerificationTokenForCustomer(Customer customer, String token);
}
