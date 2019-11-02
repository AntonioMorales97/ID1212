package client.net;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import common.Constants;
import common.MsgType;
import common.Receiver;
import common.Sender;

/**
 * Represents the connection to the server. <code>ServerConnection</code>
 * can connect, disconnect, and send requests from the client to the server.
 * When authenticated, it will hold the JSON Web Token. When connected to the
 * server, a new thread is created to listen for output from the server.
 * 
 * @author Antonio
 *
 */
public class ServerConnection {
	private static final int TIMEOUT_TEN_MIN = 600000;
	private static final int TIMEOUT_TWENTY_SEC = 20000;
	private static final String DISCONNECT = "DISCONNECT";
	private final Receiver receiver = new Receiver();
	private final Sender sender = new Sender();
	private Socket socket;
	private DataOutputStream toServer;
	private DataInputStream fromServer;
	private volatile boolean isConnected;
	private volatile String jwt = null;

	/**
	 * Connects to the server and creates a new thread to listen
	 * for server output.
	 * @param host The IP address of the server
	 * @param port The port where the server hosts its service
	 * @param handler <code>OutputHandler</code> to display server output
	 * for the client
	 * @throws IOException If error with the streams occur
	 */
	public void connect(String host, int port, OutputHandler handler) throws IOException {
		this.socket = new Socket();
		socket.connect(new InetSocketAddress(host, port), TIMEOUT_TWENTY_SEC);
		socket.setSoTimeout(TIMEOUT_TEN_MIN);
		this.isConnected = true;
		this.toServer = new DataOutputStream(socket.getOutputStream());
		this.fromServer = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		new Thread(new ServerListener(handler, this)).start();
	}

	/**
	 * Disconnect by closing the socket, i.e server connection.
	 * @throws IOException If closing the sockets failed.
	 */
	public void disconnect() throws IOException {
		sendMessage(ServerConnection.DISCONNECT);
		this.socket.close();
		this.isConnected = false;
		this.jwt = null;
		this.socket = null;
	}

	
	
	/**
	 * Sends a guess request to the server
	 * @param guess
	 */
	public void sendGuess(String guess) {
		if(!this.isConnected) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(MsgType.GUESS);
		sb.append(Constants.MSG_DELIMITER);
		sb.append(this.jwt);
		sb.append(Constants.MSG_DELIMITER);
		sb.append(guess);
		sender.sendMessageAsBytes(sb.toString(), this.toServer);
	}
	
	/**
	 * Sends a start-game request to the server
	 */
	public void sendStartGame() {
		if(!this.isConnected) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(MsgType.START);
		sb.append(Constants.MSG_DELIMITER);
		sb.append(this.jwt);
		sender.sendMessageAsBytes(sb.toString(), this.toServer);
	}
	
	/**
	 * Sends a login request to the server
	 * @param username username
	 * @param password password
	 */
	public void sendLogin(String username, String password) {
		StringBuilder sb = new StringBuilder();
		sb.append(MsgType.LOGIN);
		sb.append(Constants.MSG_DELIMITER);
		sb.append(username+Constants.MSG_BODY_DELIMETER+password);
		sender.sendMessageAsBytes(sb.toString(), this.toServer);
	}
	
	/**
	 * @return true if connected to the server; false otherwise.
	 */
	public boolean isConnected() {
		return this.isConnected;
	}
	
	private void sendMessage(String message) {
		if(!this.isConnected) {
			return;
		}
		sender.sendMessageAsBytes(message, this.toServer);
	}
	
	private void setJWT(String jwt) {
		this.jwt = jwt;
	}

	private class ServerListener implements Runnable {
		private final OutputHandler outputHandler;
		private final ServerConnection connection;

		private ServerListener(OutputHandler outputHandler, ServerConnection connection) {
			this.outputHandler = outputHandler;
			this.connection = connection;
		}

		/**
		 * Will listen for server output and if a JSON Web Token
		 * is sent it will set it for this client connection
		 */
		@Override
		public void run() {
			try {
				while(true) { 
					String respond = receiver.receiveAllBytes(fromServer);
					extractLoginJWT(respond);
					outputHandler.handleAnswer(respond);
				}
			} catch (Throwable connectionFailure) {
				if(isConnected) {
					outputHandler.handleMessage("Lost connection!");
				}
			}

		}
		
		private void extractLoginJWT(String message) {
			String[] splittedMessage = message.split(Constants.MSG_DELIMITER);
			if(MsgType.valueOf(splittedMessage[Constants.MSG_TYPE_INDEX]).equals(MsgType.LOGIN_SUCCESS)) {
				this.connection.setJWT(splittedMessage[Constants.MSG_JWT_INDEX]);
			}
		}


	}
}
