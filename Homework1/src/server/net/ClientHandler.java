package server.net;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;

import common.Constants;
import common.Receiver;
import common.Sender;
import server.controller.Controller;

/**
 * A <code>ClientHandler</code> which will handle the input -and output streams
 * from and to a client.
 * 
 * @author Antonio
 *
 */
public class ClientHandler implements Runnable{
	private volatile boolean isConnected = false;
	private final Socket socket;
	private DataOutputStream toClient;
	private DataInputStream fromClient;
	private final Sender sender = new Sender();
	private final Receiver receiver = new Receiver();
	private final Controller controller = new Controller();
	
	/**
	 * Creates a <code>ClientHandler</code>
	 * 
	 * @param socket The socket where the client is connected to
	 */
	ClientHandler(Socket socket){
		this.isConnected = true;
		this.socket = socket;
	}
	
	/**
	 * This thread will receive communication from one client and
	 * return a response to the same client
	 */
	@Override
	public void run() {
		try {
			this.toClient = new DataOutputStream(this.socket.getOutputStream());
			this.fromClient = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
			
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		while(this.isConnected) {
			try {
				String message = receiver.receiveAllBytes(this.fromClient);
				ClientMessage clientMessage = new ClientMessage(message);
				String respond;
				switch (clientMessage.getCommand()) {
				case "START":
					respond = this.controller.startGame();
					this.sender.sendMessageAsBytes(respond, this.toClient);
					break;
				case "GUESS":
					respond = this.controller.makeGuess(clientMessage.getBody());
					this.sender.sendMessageAsBytes(respond, this.toClient);
					break;
				case "DISCONNECT":
					disconnect();
					break;
				default:
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
				this.isConnected = false;
			}
		}
		
	}
	
	private void disconnect() {
		try {
			this.socket.close();
			this.isConnected = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class ClientMessage{
		private String command;
		private String body = null;
		
		public ClientMessage(String clientMessage) {
			String[] splittedMessage = clientMessage.split(Constants.MSG_DELIMITER);
			this.command = splittedMessage[Constants.MSG_COMMAND_INDEX];
			if(splittedMessage.length > 1) {
				this.body = splittedMessage[Constants.MSG_BODY_INDEX];
			}
		}
		
		public String getCommand() {
			return this.command;
		}
		
		public String getBody() {
			return this.body;
		}
	}
}
