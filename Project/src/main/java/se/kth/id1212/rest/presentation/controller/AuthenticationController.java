package se.kth.id1212.rest.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import se.kth.id1212.rest.application.exception.IllegalTransactionException;
import se.kth.id1212.rest.jwt.JwtUtil;
import se.kth.id1212.rest.presentation.models.CustomerLoginForm;
import se.kth.id1212.rest.presentation.models.CustomerLoginResponse;
import se.kth.id1212.rest.security.MyUserDetailsService;

@RestController
public class AuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private MyUserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody CustomerLoginForm loginForm)
			throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getPassword()));
		} catch(DisabledException exc) {
			throw new IllegalTransactionException("Customer is not verified. Please verify email first!");
		} 
		UserDetails userDetails = userDetailsService.loadUserByUsername(loginForm.getEmail());
		String jwt = jwtUtil.generateJwt(userDetails);
		return ResponseEntity.ok(new CustomerLoginResponse(jwt));
	}
}
