package client.net;

/**
 * Handle output from the server.
 * @author Antonio
 *
 */
public interface OutputHandler {
	/**
	 * Called when an output from the server is received.
	 * @param message the output from the server
	 */
	public void handleResponse(String message);
	
	/**
	 * Called when something with the server connection occurs
	 * and needs to notify user.
	 * @param message the message to the user
	 */
	public void handleMessage(String message);
}
