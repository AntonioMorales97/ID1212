package common;

import java.io.Serializable;

/**
 * A user data transfer object that holds some metadata of a user.
 * 
 * @author Antonio
 *
 */
public interface UserDTO extends Serializable {
	
	/**
	 * @return the username of the user.
	 */
	public String getUsername();
	
	/**
	 * @return the <code>Observer</code> of the user.
	 */
	public Observer getObserver();
}
