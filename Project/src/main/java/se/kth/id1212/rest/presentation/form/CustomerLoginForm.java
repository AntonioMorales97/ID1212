package se.kth.id1212.rest.presentation.form;

import lombok.Data;

@Data
public class CustomerLoginForm {
	private String email;
	private String password;
}
