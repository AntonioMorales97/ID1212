package client.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.CompletionException;

import client.net.OutputHandler;
import client.net.ServerConnection;

/**
 * A controller to handle requests from the view and passing it along to the net layer (connection).
 * @author Antonio
 *
 */
public class Controller {
	private final  ServerConnection connection = new ServerConnection();

	/**
	 * Asynchronous method to initialize a connection to the server by 
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
	 * Asynchronous method to start disconnection process in <code>ServerConnection</code>
	 * @throws IOException if closing connection failed
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
	 * Asynchronous method that will tell the <code>ServerConnection</code>
	 * to request a new game from the server.
	 * @throws Throwable if not connected to the server.
	 */
	public void startGame() throws Throwable {
		if(!this.connection.isConnected()) {
			throw new Throwable("Please connect first...");
		}
		CompletableFuture.runAsync(() -> {
			this.connection.sendStartGame();
		});	
	}
	
	/**
	 * Asynchronous method that will send the <code>ServerConnection</code>
	 * a guess to send to the server.
	 * @throws Throwable if not connected to the server.
	 */
	public void sendGuess(String guess) throws Throwable {
		if(!this.connection.isConnected()) {
			throw new Throwable("Please connect first...");
		}
		CompletableFuture.runAsync(() -> {
			this.connection.sendGuess(guess);
		});	
	}
	
	/**
	 * Asynchronous method that will send login credentials to the server
	 * through the <code>ServerConnection</code>.
	 * @param username username
	 * @param password password
	 * @throws Throwable if not connected to the server
	 */
	public void login(String username, String password) throws Throwable {
		if(!this.connection.isConnected()) {
			throw new Throwable("Please connect first...");
		}
		CompletableFuture.runAsync(() -> {
			this.connection.sendLogin(username, password);
		});	
	}
}
