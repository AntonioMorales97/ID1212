package se.kth.id1212.rest.presentation.cust;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import se.kth.id1212.rest.util.OrderStatus;
import se.kth.id1212.rest.util.ValueOfEnum;

public class OrderUpdateForm {
	@NotNull(message = "Updated order status is missing")
	@NotBlank(message = "Updated order status is blank")
	@ValueOfEnum(enumClass = OrderStatus.class, message="Not supported order status")
	private String updatedStatus;
	
	public void setUpdatedStatus(String updatedStatus) {
		this.updatedStatus = updatedStatus.toUpperCase();
	}
	
	public String getUpdatedStatus() {
		return this.updatedStatus;
	}
}
