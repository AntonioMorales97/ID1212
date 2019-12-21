package se.kth.id1212.rest.presentation.models;

import lombok.Data;

/**
 * Login response, i.e returns the generated JSON Web Token when authenticated.
 * 
 * @author Antonio
 *
 */
@Data
public class CustomerLoginResponse {
	private String jwt;

	public CustomerLoginResponse(String jwt) {
		this.jwt = jwt;
	}
}
