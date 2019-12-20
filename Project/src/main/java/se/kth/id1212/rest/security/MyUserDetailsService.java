package se.kth.id1212.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.kth.id1212.rest.domain.Customer;
import se.kth.id1212.rest.repository.CustomerRepository;

/**
 * Implements <code>UserDetailsService</code>. Used for authentication of <code>Customer</code>s.
 * 
 * @author Antonio
 *
 */
@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService{

	@Autowired
	private CustomerRepository customerRepo;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Customer customer = this.customerRepo.findByEmail(email);
		if(customer == null)
			throw new UsernameNotFoundException("Customer with email: " + email + ", was not found!");

		return new MyUserPrincipal(customer);
	}

}
