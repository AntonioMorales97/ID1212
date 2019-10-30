package server.net;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;

import common.Receiver;
import common.Sender;

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
				System.out.println(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); //remove this 
				this.isConnected = false;
			}
		}
		
	}
	
	private void configStreams() {
		
	}
}
