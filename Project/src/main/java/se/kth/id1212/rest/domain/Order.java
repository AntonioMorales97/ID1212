package se.kth.id1212.rest.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import se.kth.id1212.rest.application.exception.IllegalTransactionException;
import se.kth.id1212.rest.enums.OrderStatus;

/**
 * Represents a <code>Customer</code>'s order. The <code>@Data</code> will create all the getters,
 * setters, <code>equals</code>, <code>hash</code>, and <code>toString</code> methods.
 * 
 * @author Antonio
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name = "customer_order")
public class Order extends RepresentationModel<Order> {

	private static final String SEQ_NAME_KEY = "SEQ_NAME";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME_KEY)
	@SequenceGenerator(name = SEQ_NAME_KEY, sequenceName = "ORDER_SEQUENCE")
	private Long id;

	@NotNull(message = "{order.customer.missing}")
	@ManyToOne
	@JoinColumn(name = "customer_id")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	//@JsonManagedReference
	@JsonIgnoreProperties({"age", "firstName", "lastName"})
	private Customer customer;

	@NotNull(message = "{order.status.missing}")
	@Setter(AccessLevel.NONE)
	private OrderStatus status;

	@NotNull(message = "{order.price.missing}")
	@PositiveOrZero(message = "Price must be zero or greater")
	private Double price;

	@NotNull(message = "{order.description.missing}")
	@NotBlank(message = "{order.description.blank}")
	private String description;

	/**
	 * Required by JPA (don't use!)
	 */
	protected Order() {}

	/**
	 * Creates an instance of this class without setting the <code>Customer</code>.
	 * 
	 * @param status The status of the order.
	 * @param price The price of the order.
	 * @param description The description of the order.
	 */
	public Order(OrderStatus status, Double price, String description) {
		this.status = status;
		this.price = price;
		this.description = description;
	}

	/**
	 * Creates an instance of this class with the <code>Customer</code>.
	 * 
	 * @param status The status of the order.
	 * @param price The price of the order.
	 * @param description The description of the order.
	 * @param customer The <code>Customer</code> that ordered this order.
	 */
	public Order(OrderStatus status, Double price, String description, Customer customer) {
		this.status = status;
		this.price = price;
		this.description = description;
		this.customer = customer;
	}

	/**
	 * Updates the status of this order.
	 * 
	 * @param status The new status.
	 */
	public void setStatus(OrderStatus status) {
		if((status == OrderStatus.CANCELLED) && (this.status == OrderStatus.COMPLETED))
			throw new IllegalTransactionException("Cannot cancel an order with the order status being " + OrderStatus.COMPLETED);
		if((status == OrderStatus.COMPLETED) && (this.status == OrderStatus.CANCELLED))
			throw new IllegalTransactionException("Cannot complete an order with order status being " + OrderStatus.CANCELLED);

		this.status = status;
	}

	/**
	 * Checks if this order is removable or not, i.e is not active.
	 * 
	 * @return <code>true</code> if removable; <code>false</code> otherwise.
	 */
	@JsonIgnore
	public boolean isRemovable() {
		if(this.status == OrderStatus.IN_PROGRESS)
			return false;
		return true;
	}

}
