package client.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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
	 */
	public void disconnect() {
		CompletableFuture.runAsync(() -> {
			this.connection.disconnect();
		});
	}

	/**
	 * Async method to send a message to the server by calling <code>ServerConnection</code>
	 * @param message
	 */
	public void sendMessage(String message) throws Throwable {
		if(!this.connection.isConnected()) {
			throw new Throwable("Please connect first...");
		}
		CompletableFuture.runAsync(() -> {
			
				try {
					this.connection.sendMessage(message);
				} catch (Exception e) {
					System.out.println("catched");
					throw new CompletionException(e);
				}
			

		});
	}
}
