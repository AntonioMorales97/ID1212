package client.net;

/**
 * Handle output from the server and notify the view.
 * @author Antonio
 *
 */
public interface CommunicationListener {
	/**
	 * Called whenever a connection was successful
	 */
	public void connected();
	/**
	 * Called whenever a disconnection has occurred
	 */
	public void disconnected();
	/**
	 * Called whenever a response from the server has been retrieved
	 * @param msg The response from the server
	 */
	public void receivedMessage(String msg);
	/**
	 * Called to notify the view
	 * @param msg The message to notify the view with
	 */
	public void handleMessage(String msg);
}
