package se.kth.id1212.rest.presentation.cust;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.Data;

/**
 * Represents a form to create new <code>Order</code>s.
 * 
 * @author Antonio
 *
 */
@Data
class OrderForm {

	@NotNull(message = "{order.price.missing}")
	@PositiveOrZero(message = "Price must be zero or greater")
	private Double price;

	@NotNull(message = "{order.description.missing}")
	@NotBlank(message = "{order.description.blank}")
	private String description;

}
