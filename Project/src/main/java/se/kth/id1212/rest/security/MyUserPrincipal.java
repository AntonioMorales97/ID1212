package se.kth.id1212.rest.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import se.kth.id1212.rest.domain.Customer;

/**
 * Implements <code>UserDetails</code>. Used for authentication and stores the <code>Customer</code> to 
 * authenticate as core information.
 * 
 * @author Antonio
 *
 */
public class MyUserPrincipal implements UserDetails{
	private static final long serialVersionUID = 1L;
	private Customer customer;

	/**
	 * Creates an instance of this class with the stored <code>Customer</code>.
	 * 
	 * @param customer The <code>Customer</code>.
	 */
	public MyUserPrincipal(Customer customer) {
		this.customer = customer;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		final List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("User"));
		return authorities;
	}

	@Override
	public String getPassword() {
		return this.customer.getPassword();
	}

	@Override
	public String getUsername() {
		return this.customer.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.customer.isVerified();
	}

	/**
	 * @return the stored <code>Customer</code> in this principal.
	 */
	public Customer getCustomer() {
		return this.customer;
	}

	/**
	 * @return the ID of the stored <code>Customer</code> in this principal.
	 */
	public Long getCustomerId() {
		return this.customer.getId();
	}
}
