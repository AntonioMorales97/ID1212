package client.net;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import common.Receiver;
import common.Sender;

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

	public void connect(String host, int port, OutputHandler handler) throws IOException {
		this.socket = new Socket();
		socket.connect(new InetSocketAddress(host, port), TIMEOUT_TWENTY_SEC);
		socket.setSoTimeout(TIMEOUT_TEN_MIN);
		this.isConnected = true;
		this.toServer = new DataOutputStream(socket.getOutputStream());
		this.fromServer = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		new Thread(new ServerListener(handler)).start();
	}

	/**
	 * Disconnect by closing the socket, i.e server connection.
	 * @throws IOException If closing the sockets failed.
	 */
	public void disconnect() throws IOException {
		sendMessage(ServerConnection.DISCONNECT);
		this.socket.close();
		this.isConnected = false;	
		this.socket = null;
	}

	public void sendMessage(String message) {
		if(!this.isConnected) {
			return;
		}
		sender.sendMessageAsBytes(message, this.toServer);
	}

	public boolean isConnected() {
		return this.isConnected;
	}

	private class ServerListener implements Runnable {
		private final OutputHandler outputHandler;

		private ServerListener(OutputHandler outputHandler) {
			this.outputHandler = outputHandler;
		}

		@Override
		public void run() {
			try {
				while(true) {
					outputHandler.handleAnswer(receiver.receiveAllBytes(fromServer));
				}
			} catch (Throwable connectionFailure) {
				if(isConnected) {
					outputHandler.handleMessage("Lost connection!");
				}
			}

		}


	}
}
