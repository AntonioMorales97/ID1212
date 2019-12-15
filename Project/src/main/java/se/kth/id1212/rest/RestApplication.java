package se.kth.id1212.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starts the spring application.
 * 
 * @author Antonio
 *
 */

@SpringBootApplication
public class RestApplication {
	
	/**
	 * Starts the rest application.
	 * 
	 * @param args Not used here.
	 */
	public static void main(String[] args) {
		SpringApplication.run(RestApplication.class, args);
	}

}
