package client.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.CompletionException;

import client.net.OutputHandler;
import client.net.ServerConnection;

/**
 * A Controller to handle requests from the view and passing it along to the net layer (connection).
 * @author Antonio
 *
 */
public class Controller {
	private final  ServerConnection connection = new ServerConnection();

	/**
	 * Async method to initialize a connection to the server by 
	 * calling the connect method in <code>ServerConnection</code>
	 * @param host The host to start a connection to
	 * @param port The port in which the host will receive the connection
	 * @param outputHandler An <code>OutputHandler</code> to handle output from the server
	 */
	public void connect(String host, int port, OutputHandler outputHandler) {
		CompletableFuture.runAsync(() -> {
			try {
				this.connection.connect(host, port, outputHandler);
			} catch (IOException exc) {
				outputHandler.handleMessage("Failed to connect to server");
				throw new UncheckedIOException(exc);
			}
		}).thenRun(() -> {
			outputHandler.handleMessage("Successfully connected to server");
		});
	}

	/**
	 * Async method to start disconnection process in <code>ServerConnection</code>
	 * @throws IOException 
	 */
	public void disconnect() throws IOException {
		this.connection.disconnect();
	}
	
	/**
	 * Check if the <code>ServerConnection</code> is connected or not.
	 * @return true or false depending if it is connected to the server or not.
	 */
	public boolean isConnectedToServer() {
		return this.connection.isConnected();
	}

	/**
	 * Async method to send a message to the server by calling <code>ServerConnection</code>
	 * @param message The message to be sent to the server
	 * @throws Throwable if unable to send message.
	 */
	public void sendMessage(String message) throws Throwable {
		if(!this.connection.isConnected()) {
			throw new Throwable("Please connect first...");
		}
		CompletableFuture.runAsync(() -> {
			this.connection.sendMessage(message);
		});
	}
}
