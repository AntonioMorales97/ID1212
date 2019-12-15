package se.kth.id1212.rest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import se.kth.id1212.rest.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
	public List<Order> findAllByCustomerId(Long customerId);
}
