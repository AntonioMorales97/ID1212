package server.net;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;

import common.Constants;
import common.MsgType;
import common.Receiver;
import common.Sender;
import server.controller.Controller;

/**
 * A <code>ClientHandler</code> which will handle the input -and output streams
 * from and to a client and interpret messages.
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
	 * This thread will receive communication from one client, translate it, and
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
					try {
						if(clientMessage.getEncodedJwt() == null) {
							sendToClient(MsgType.INVALID_REQUEST, "Please login!");
							break;
						}
						this.controller.validJwt(clientMessage.getEncodedJwt());
						respond = this.controller.startGame();
						sendToClient(MsgType.GAME_RESPOND, respond);
					} catch(Throwable e) {
						sendToClient(MsgType.INVALID_REQUEST, e.getMessage());
					}	
					break;
				case "GUESS":
					try {
						if(clientMessage.getEncodedJwt() == null) {
							sendToClient(MsgType.INVALID_REQUEST, "Please login and start a game!");
							break;
						}
						this.controller.validJwt(clientMessage.getEncodedJwt());
						respond = this.controller.makeGuess(clientMessage.getBody());
						sendToClient(MsgType.GAME_RESPOND, respond);
					} catch(Throwable e) {
						sendToClient(MsgType.INVALID_REQUEST, e.getMessage());
					}	
					break;
				case "DISCONNECT":
					disconnect();
					break;
				case "LOGIN":
					String username = clientMessage.getUsername();
					String password = clientMessage.getPassword();
					try {
						String encodedJwt = this.controller.login(username, password);
						sendToClient(MsgType.LOGIN_SUCCESS, encodedJwt);
					} catch (Throwable e) {
						sendToClient(MsgType.LOGIN_FAIL, e.getMessage());
					}
					break;
				default:
					break;
				}
			} catch (IOException e) {
				System.out.println("Client disconnected");
				//e.printStackTrace();
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
	
	private void sendToClient(MsgType msgType, String msg) {
		this.sender.sendMessageAsBytes(msgType+Constants.MSG_DELIMITER+msg, this.toClient);
	}
	
	private class ClientMessage{
		private String command;
		private String encodedJwt = null;
		private String body = null;
		private String username = null;
		private String password = null;
		private String[] splittedMessage;
		
		private ClientMessage(String clientMessage) {
			this.splittedMessage = clientMessage.split(Constants.MSG_DELIMITER);
			this.command = this.splittedMessage[Constants.MSG_TYPE_INDEX];
			switch (MsgType.valueOf(this.command)) {
			case LOGIN:
				this.body = getParameter(Constants.MSG_BODY_INDEX);
				String[] credentials = this.body.split(Constants.MSG_BODY_DELIMETER);
				this.username = credentials[Constants.MSG_BODY_USERNAME_INDEX];
				this.password = credentials[Constants.MSG_BODY_PASSWORD_INDEX];
				break;
			case DISCONNECT:
				break;
			case START:
				setEncodedJwt();
				break;
			case GUESS:
				setEncodedJwt();
				this.body = getParameter(Constants.MSG_BODY_GUESS_INDEX);
				break;
			default:
				break;
			}	
		}
		
		private String getCommand() {
			return this.command;
		}
		
		private String getBody() {
			return this.body;
		}
		
		private String getEncodedJwt() {
			return this.encodedJwt;
		}
		
		private String getUsername() {
			return this.username;
		}
		
		private String getPassword() {
			return this.password;
		}
		
		private void setEncodedJwt() {
			if(this.splittedMessage[Constants.MSG_JWT_INDEX].equals("null")) {
				return;
			}
			this.encodedJwt = this.splittedMessage[Constants.MSG_JWT_INDEX];
		}
		
		private String getParameter(int index) {
			if(index >= this.splittedMessage.length) {
				return null;
			}
			return this.splittedMessage[index];
		}
	}
}
