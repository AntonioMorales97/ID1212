package se.kth.id1212.rest.domain;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import se.kth.id1212.rest.util.CustomerMembership;
import se.kth.id1212.rest.util.OrderStatus;
import se.kth.id1212.rest.util.ValueOfEnum;

/**
 * Represents a customer. The <code>@Data</code> will create all the getters,
 * setters, <code>equals</code>, <code>hash</code>, and <code>toString</code> methods.
 * 
 * @author Antonio
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
@Entity
public class Customer extends RepresentationModel<Customer> {
	private static final String SEQ_NAME_KEY = "SEQ_NAME";
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME_KEY)
	@SequenceGenerator(name = SEQ_NAME_KEY, sequenceName = "CUSTOMER_SEQUENCE")
	private Long id;
	
	@Pattern(regexp = "^[\\p{L}\\p{M}*]*$", message = "Only letters are allowed")
	@NotNull(message = "{customer.firstName.missing}")
	@NotBlank(message = "{customer.firstName.blank}")
	private String firstName;
	
	@Pattern(regexp = "^[\\p{L}\\p{M}*]*$", message = "Only letters are allowed")
	@NotNull(message = "{customer.lastName.missing}")
	@NotBlank(message = "{customer.lastName.blank}")
	private String lastName;
	
	@NotBlank(message = "{customer.personalNumber.blank}")
	@NotNull(message = "{customer.personalNumber.missing}")
	@Pattern(regexp="[\\d]{10}", message = "Personal number must be 10 digits")
	@Column(unique=true)
	private String personalNumber;
	
	@NotNull(message = "{customer.age.missing}")
	@PositiveOrZero(message = "Age must be zero or greater")
	private Integer age;
	
	@Pattern(regexp = "^[\\p{L}\\p{M}*]*$", message = "Only letters are allowed")
	@NotNull(message = "{customer.membership.missing}")
	@NotBlank(message = "{customer.membership.blank}")
	@ValueOfEnum(enumClass = CustomerMembership.class, message = "Not supported customer membership")
	@Setter(AccessLevel.NONE) //we need to manually convert to upper case
	private String membership;
	
	@OneToMany(cascade = CascadeType.ALL,
			fetch = FetchType.EAGER,
			mappedBy = "customer",
			orphanRemoval = true)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@JsonBackReference
	private Set<Order> orders = new HashSet<>();
	
	/**
	 * Required by JPA (don't use!)
	 */
	protected Customer() {}
	
	/**
	 * Creates an instance of this class.
	 * 
	 * @param firstName The first name.
	 * @param lastName The last name.
	 * @param age The age.
	 * @param membership The type of membership.
	 */
	public Customer(String firstName, String lastName, String personalNumber, int age, String membership) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.personalNumber = personalNumber;
		this.age = age;
		this.membership = membership.toUpperCase();
	}
	
	/**
	 * Sets the membership with upper case.
	 * 
	 * @param membership the membership.
	 */
	public void setMembership(String membership) {
		this.membership = membership.toUpperCase();
	}
	
	public boolean isRemovable() {
		if(this.orders != null) {
			for(Order order : this.orders) {
				if(order.getStatus() == OrderStatus.IN_PROGRESS)
					return false;
			}
		}
		return true;
	}
}
