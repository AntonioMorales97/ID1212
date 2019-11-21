package client.view;

/**
 * User commands.
 * 
 * @author Antonio
 *
 */
public enum Command {
	/**
	 * Command to register user.
	 */
	REGISTER,
	/**
	 * Command to login with an existing user.
	 */
	LOGIN,
	/**
	 * Command to logout.
	 */
	LOGOUT,
	/**
	 * Command to upload a file.
	 */
	UPLOAD,
	/**
	 * Command to download a file.
	 */
	DOWNLOAD,
	/**
	 * Command to update an existing file.
	 */
	UPDATE,
	/**
	 * Command to delete a file.
	 */
	DELETE,
	/**
	 * Command to list all files in the database.
	 */
	LIST,
	/**
	 * Command to quit the client.
	 */
	QUIT,
	/**
	 * Command to show all the commands.
	 */
	HELP,
	/**
	 * Invalid commands are set to this.
	 */
	COMMAND_ERROR
}
