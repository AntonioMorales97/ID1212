package client.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import common.Constants;
import common.MessageType;
import common.Receiver;
import common.Sender;

/**
 * Represents the connection to the server. <code>ServerConnection</code>
 * can connect, disconnect, and send requests from the client to the server.
 *
 * @author Antonio
 *
 */
public class ServerConnection {
	private static final int TIMEOUT_TEN_MIN = 600000;
	private static final int TIMEOUT_TWENTY_SEC = 20000;
	private final Receiver receiver = new Receiver();
	private final Sender sender = new Sender();
	private Socket socket;
	private BufferedOutputStream toServer;
	private BufferedInputStream fromServer;
	private volatile boolean isConnected;
	
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
		this.toServer =new BufferedOutputStream(new DataOutputStream(this.socket.getOutputStream()));
		this.fromServer = new BufferedInputStream(new DataInputStream(this.socket.getInputStream()));
		new Thread(new ServerListener(handler)).start();
	}

	public void uploadFile(String fileName, String content) {
		this.sender.sendMessage(MessageType.UPLOAD + Constants.MSG_DELIMITER +
				fileName + Constants.MSG_BODY_DELMITER + content, this.toServer);
	}
	
	public void deleteFile(String fileName) {
		this.sender.sendMessage(MessageType.DELETE + Constants.MSG_DELIMITER +
				fileName, this.toServer);
	}
	
	public void downloadFile(String fileName) {
		this.sender.sendMessage(MessageType.DOWNLOAD + Constants.MSG_DELIMITER
				+ fileName, this.toServer);
	}

	/**
	 * @return true if connected to the server; false otherwise.
	 */
	public boolean isConnected() {
		return this.isConnected;
	}
	
	public void disconnect() {
		this.isConnected = false;
		try {
			this.sender.sendMessage(MessageType.DISCONNECT.toString(), this.toServer);
			this.toServer.close();
			this.fromServer.close();
			this.socket.close();
		} catch (IOException e) {
			System.err.println("Failed to disconnect");
			e.printStackTrace();
		}
		
	}
	
	private class ServerListener implements Runnable {
		private final OutputHandler outputHandler;
		
		private ServerListener(OutputHandler outputHandler) {
			this.outputHandler = outputHandler;
		}

		/**
		 * Will listen for server responses.
		 */
		@Override
		public void run() {
			try {
				while(true) { 
					String response = receiver.receiveMessage(fromServer);
					outputHandler.handleResponse(response);
				}
			} catch (Throwable connectionFailure) {
				if(isConnected) {
					outputHandler.handleMessage("Lost connection!");
				}
			}

		}
	}
}
