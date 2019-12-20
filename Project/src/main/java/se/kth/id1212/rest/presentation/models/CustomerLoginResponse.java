package se.kth.id1212.rest.presentation.models;

import lombok.Data;

@Data
public class CustomerLoginResponse {
	private String jwt;
	
	public CustomerLoginResponse(String jwt) {
		this.jwt = jwt;
	}
}
