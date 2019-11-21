package server.model;

import java.util.ArrayList;

import common.UserDTO;

/**
 * Manages logged in clients.
 * 
 * @author Antonio
 *
 */
public class UserManager {
	private ArrayList<UserDTO> loggedInUsers = new ArrayList<>();
	
	/**
	 * Stores the given user to the other logged in users.
	 * 
	 * @param user an <code>UserDTO</code>, representing the user.
	 */
	public void loginUser(UserDTO user) {
		if(isLoggedIn(user))
			return;
		this.loggedInUsers.add(user);
	}
	
	/**
	 * Remove the given user from the logged in users.
	 * 
	 * @param user an <code>UserDTO</code>, representing the user to be removed.
	 */
	public void logoutUser(UserDTO user) {
		this.loggedInUsers.remove(user);
	}
	
	/**
	 * Checks if the given user is logged in, i.e among the other logged users.
	 * 
	 * @param user an <code>UserDTO</code>, representing the user to be removed.
	 * @return <code>true</code> if the user is logged in; <code>false</code> otherwise.
	 */
	public boolean isLoggedIn(UserDTO user) {
		return this.loggedInUsers.contains(user);
	}
	
	/**
	 * Tries to return the <code>UserDTO</code> in the logged in users with the given username.
	 * 
	 * @param username the given username.
	 * @return an <code>UserDTO</code> if one is found with the given username; <code>null</code>
	 * otherwise.
	 */
	public UserDTO getUser(String username) {
		UserDTO dummyUser = new User(username, null);
		int index = this.loggedInUsers.indexOf(dummyUser);
		if(index == -1)
			return null;
		return this.loggedInUsers.get(index);
	}
	
}
