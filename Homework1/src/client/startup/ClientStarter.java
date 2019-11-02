package client.startup;

import client.view.ConsoleInput;

/**
 * Client starter; connection will be to localhost:5000.
 * 
 * @author Antonio
 *
 */
public class ClientStarter {
	private static final int PORT = 5000;
	private static final String LOCALHOST = "127.0.0.1";

	/**
	 * Starts the client.
	 * @param args
	 */
	public static void main(String[] args) {
		//System.out.println("Starting Client...");
		new ConsoleInput(LOCALHOST, PORT).start();
	}

}
