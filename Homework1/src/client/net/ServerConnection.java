package client.net;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import common.Receiver;

public class ServerConnection {
	private static final int TIMEOUT_TEN_MIN = 600000;
	private static final int TIMEOUT_TWENTY_SEC = 20000;
	private final Receiver receiver = new Receiver();
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
	 */
	public void disconnect() {
		System.out.println("Disconnecting...");
		try {
			this.socket.close();
			System.out.println("Disconnected. Bye!");
			this.socket = null;
			this.isConnected = false;
		} catch (IOException e) {
			System.err.println("Unable to disconnect:");
			System.err.println(e.getMessage());
			System.err.println("Exiting...");
			System.exit(1);
		}
		
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
