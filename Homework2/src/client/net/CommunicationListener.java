package client.net;

/**
 * Handle output from the server.
 * @author Antonio
 *
 */
public interface CommunicationListener {
	public void connected();
	public void disconnected();
	public void receivedMessage(String msg);
}
