package server.model;

import common.Observer;
import common.UserDTO;

/**
 * Holds the username and the <code>Observer</code> of a client.
 * 
 * @author Antonio
 *
 */
public class User implements UserDTO{
	private String username;
	private Observer observer;
	
	/**
	 * Creates an instance of this class with the given username and <code>Observer</code>
	 * of the client.
	 * 
	 * @param username the username of the client.
	 * @param observer the <code>Observer</code> of the client.
	 */
	public User(String username, Observer observer) {
		this.username = username;
		this.observer = observer;
	}

	/**
	 * @return the username of this user.
	 */
	@Override
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * @return the <code>Observer</code> of this user.
	 */
	@Override
	public Observer getObserver() {
		return this.observer;
	}
	
	/**
	 * Compares if the given <code>Object</code> is equal to this
	 * instance.
	 * @return <code>true</code> if equal; <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		
		if(!(obj instanceof UserDTO))
			return false;
		
		UserDTO user = (UserDTO) obj;
		return user.getUsername().equals(this.username);
	}

}
