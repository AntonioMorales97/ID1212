package server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * The class <code>HangmanServer</code> will run in a specified port and manage
 * new clients by creating new client sockets and starting a new thread to the
 * {@link ClientHandler}.
 * 
 * @author Antonio
 *
 */
public class HangmanServer {
	private static final int TIMEOUT_TEN_MIN = 600000;
	private static final int LINGER_TIME = 5000;
	
	private int port = 8080;
	
	/**
	 * Creates a <code>HangmanServer</code>
	 * @param port The port where the server will be started on.
	 */
	public HangmanServer(int port) {
		this.port = port;
	}
	
	/**
	 * Starts the <code>HangmanServer</code> by creating a listening <code>ServerSocket</code>
	 * that listens for new connections from clients.
	 */
	public void run() {
		try {
			System.out.println("Server socket on port: " + this.port);
			ServerSocket listeningSocket = new ServerSocket(this.port); 
			while(true) {
				Socket clientSocket = listeningSocket.accept();
				startClientHandler(clientSocket);
			}
			
		} catch(IOException exc) {
			System.err.println("Caught exception from HangmanServer:");
			exc.printStackTrace();
		}
		
	}
	
	private void startClientHandler(Socket clientSocket) throws SocketException {
		clientSocket.setSoLinger(true, LINGER_TIME);
		clientSocket.setSoTimeout(TIMEOUT_TEN_MIN);
		ClientHandler handler = new ClientHandler(clientSocket);
		Thread clientHandlerThread = new Thread(handler);
		clientHandlerThread.setPriority(Thread.MAX_PRIORITY);
		clientHandlerThread.start();
	}
}
