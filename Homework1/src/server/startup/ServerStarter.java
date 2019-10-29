package server.startup;

import server.net.HangmanServer;

public class ServerStarter {
	private static final int PORT = 5000;
	
	public static void main(String[] args) {
		HangmanServer server = new HangmanServer(PORT);
		server.run();
	}

}
