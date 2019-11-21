package server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * A server for managing connected client sockets for file uploading, deletion, and
 * downloading.
 * 
 * @author Antonio
 *
 */
public class Server {
	private static final int TIMEOUT_TEN_MIN = 600000;
	private static final int LINGER_TIME = 5000;
	
	private int port = 8000;
	
	/**
	 * Creates a <code>Server</code> with the given port.
	 * 
	 * @param port the given port.
	 */
	public Server(int port) {
		this.port = port;
	}
	
	/**
	 * Starts this server.
	 */
	public void start() {
		try {
			ServerSocket serverSocket = new ServerSocket(this.port);
			System.out.println("Server started on port: " + this.port);
			while(true) {
				Socket clientSocket = serverSocket.accept();
				startClientHandler(clientSocket);
			}
		} catch (IOException e) {
			e.printStackTrace();
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
