package se.kth.id1212.rest.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import se.kth.id1212.rest.domain.Customer;
import se.kth.id1212.rest.domain.Order;
import se.kth.id1212.rest.enums.OrderStatus;
import se.kth.id1212.rest.repository.CustomerRepository;
import se.kth.id1212.rest.repository.OrderRepository;

/**
 * Will initialize the database with some dummy data. The <code>@Slf4j</code>
 * will let us see the initialization in the log.
 * 
 * @author Antonio
 *
 */
@Configuration
@Slf4j
class DatabaseInitializer {

	@Bean
	CommandLineRunner initializeDatabase(CustomerRepository customerRepo, OrderRepository orderRepo) {

		Customer c1 = new Customer("John", "Doe", "8906131234", 30, "GOLD");
		Customer c2 = new Customer("Jane", "Doe", "8910211223" ,30, "BRONZE");
		Customer c3 = new Customer("Freddy", "Krueger", "6901243031", 50, "SILVER");
		Customer c4 = new Customer("Penny", "Jenny", "9908281447", 20, "GOLD");
		Customer c5 = new Customer("Zlatan", "Ibrahimovic", "8501011359", 34, "GOLD");
		Customer c6 = new Customer("Tiger", "Woods", "7404012479", 45, "BRONZE");
		c1.setEmail("test1@mail.com");
		c1.setPassword("test");
		c2.setEmail("test2@mail.com");
		c2.setPassword("test");
		c3.setEmail("test3@mail.com");
		c3.setPassword("test");
		c4.setEmail("test4@mail.com");
		c4.setPassword("test");
		c5.setEmail("test5@mail.com");
		c5.setPassword("test");
		c6.setEmail("test6@mail.com");
		c6.setPassword("test");
		c1.setVerified(true);
		c2.setVerified(true);
		c3.setVerified(true);
		c4.setVerified(true);
		c5.setVerified(true);
		c6.setVerified(true);
		Order o1 = new Order(OrderStatus.IN_PROGRESS, 20.5, "Iphone");
		o1.setCustomer(c6);
		c6.getOrders().add(o1);
		return args -> {
			log.info("Inserting " + customerRepo.save(c1));
			log.info("Inserting " + customerRepo.save(c2));
			log.info("Inserting " + customerRepo.save(c3));
			log.info("Inserting " + customerRepo.save(c4));
			log.info("Inserting " + customerRepo.save(c5));
			log.info("Inserting " + customerRepo.save(c6));
			//log.info("Inserting " + orderRepo.save(o1)); //saved through customer
		};
	}
}
