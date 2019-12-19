package se.kth.id1212.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import se.kth.id1212.rest.domain.Customer;
import se.kth.id1212.rest.domain.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	public VerificationToken findByToken(String token);
	
	public void deleteByCustomer(Customer customer);
}
