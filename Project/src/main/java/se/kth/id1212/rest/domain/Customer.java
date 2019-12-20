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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import se.kth.id1212.rest.enums.CustomerMembership;
import se.kth.id1212.rest.enums.OrderStatus;
import se.kth.id1212.rest.validation.ValidEmail;
import se.kth.id1212.rest.validation.ValueOfEnum;

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

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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

	@ValidEmail
	@NotNull(message = "{customer.email.missing}")
	@NotBlank(message = "{customer.email.blank}")
	private String email;

	@NotNull(message = "{customer.password.missing}")
	@NotBlank(message = "{customer.password.blank}")
	private String password;

	@NotNull
	private Boolean verified;

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
		this.verified = false;
	}

	/**
	 * Sets the membership with upper case.
	 * 
	 * @param membership the membership.
	 */
	public void setMembership(String membership) {
		this.membership = membership.toUpperCase();
	}

	/**
	 * Checks if the <code>Customer</code> is removable or not, i.e does not have
	 * any active <code>Order</code>s.
	 * 
	 * @return <code>true</code> if removable; <code>false</code> otherwise.
	 */
	@JsonIgnore
	public boolean isRemovable() {
		if(this.orders != null) {
			for(Order order : this.orders) {
				if(order.getStatus() == OrderStatus.IN_PROGRESS)
					return false;
			}
		}
		return true;
	}

	/**
	 * @return the <code>true</code> if the <code>Customer</code> is verified; <code>false</code> otherwise.
	 */
	public boolean isVerified() {
		return this.verified;
	}
}
