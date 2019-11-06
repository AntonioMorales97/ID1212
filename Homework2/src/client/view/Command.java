package client.view;

/**
 * User commands.
 * @author Antonio
 *
 */
public enum Command {
	/**
	 * Command to connect to server.
	 */
	CONNECT,
	
	/**
	 * Command to make a guess in the game.
	 */
	GUESS,
	
	/**
	 * Command to disconnect from the server.
	 */
	DISCONNECT,
	
	/**
	 * Command to start a new game.
	 */
	START,
	
	/**
	 * Command to display command information to the user.
	 */
	HELP,
	
	/**
	 * Command to quit the client.
	 */
	QUIT,
	/**
	 * Command to display information about the game.
	 */
	INFO,
	/**
	 * Invalid commands are set to this.
	 */
	COMMAND_ERROR
}
