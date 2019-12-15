package se.kth.id1212.rest.presentation.error;

/**
 * Represents a constraint violation with the field that got violated
 * and the message. Used to return to the client.
 * 
 * @author Antonio
 *
 */
class Violation {
	private final String fieldName;
	private final String message;

	/**
	 * Creates an instance of <code>Violation</code>.
	 * 
	 * @param fieldName The field that got violated.
	 * @param message The message for that violation.
	 */
	Violation(String fieldName, String message) {
		this.fieldName = fieldName;
		this.message = message;
	}

	/**
	 * @return the field name.
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @return the violation message.
	 */
	public String getMessage() {
		return message;
	}
}
