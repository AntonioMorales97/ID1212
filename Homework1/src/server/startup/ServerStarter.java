package server.startup;

import server.net.HangmanServer;

/**
 * Just a server starter. Starts the <code>HangmanServer</code> at 
 * port 5000.
 * 
 * @author Antonio
 *
 */
public class ServerStarter {
	private static final int PORT = 5000;
	
	/**
	 * Start the <code>HangmanServer</code>
	 * @param args Not used
	 */
	public static void main(String[] args) {
		HangmanServer server = new HangmanServer(PORT);
		server.run();
	}

}
