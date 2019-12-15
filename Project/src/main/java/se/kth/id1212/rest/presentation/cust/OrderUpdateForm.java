package se.kth.id1212.rest.presentation.cust;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import se.kth.id1212.rest.util.OrderStatus;
import se.kth.id1212.rest.util.ValueOfEnum;

/**
 * Represents an update form to update an <code>Order</code>.
 * 
 * @author Antonio
 *
 */
@Deprecated
public class OrderUpdateForm {
	@NotNull(message = "Updated order status is missing")
	@NotBlank(message = "Updated order status is blank")
	@ValueOfEnum(enumClass = OrderStatus.class, message="Not supported order status")
	private String updatedStatus;

	/**
	 * Sets the updated status to the new given updated status in upper case.
	 * 
	 * @param updatedStatus The new updated status.
	 */
	public void setUpdatedStatus(String updatedStatus) {
		this.updatedStatus = updatedStatus.toUpperCase();
	}

	/**
	 * Gets the updated status.
	 * 
	 * @return the updated status.
	 */
	public String getUpdatedStatus() {
		return this.updatedStatus;
	}
}
