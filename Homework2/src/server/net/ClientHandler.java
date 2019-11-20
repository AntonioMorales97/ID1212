package server.net;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import common.Constants;
import common.MessageDivider;
import common.MessageType;
import server.controller.Controller;

/**
 * A <code>ClientHandler</code> which handles communication with one client only.
 * 
 * @author Antonio
 *
 */
public class ClientHandler {
	private final HangmanServer server;
	private final SocketChannel clientChannel;
	private final ByteBuffer fromClient = ByteBuffer.allocate(Constants.MAX_MSG_LENGTH);
	private final MessageDivider messageDivider = new MessageDivider();
	private final Controller controller = new Controller();
	
	/**
	 * Creates a <code>ClientHandler</code> with a reference to the server
	 * and a <code>SocketChannel</code> to the client.
	 * @param server the server
	 * @param socketChannel the <code>SocketChannel</code> to the client.
	 */
	ClientHandler(HangmanServer server, SocketChannel socketChannel){
		this.clientChannel = socketChannel;
		this.server = server;
	}
	
	/**
	 * Runs whenever a message is received from the client to act accordingly.
	 */
	public void run() {
		while(this.messageDivider.hasNext()) {
			ClientMessage clientMessage = new ClientMessage(this.messageDivider.nextMessage());
			String response;
			switch (MessageType.valueOf(clientMessage.msgType)) {
			case START:
				response = this.controller.startGame();
				this.server.respondToClient(this.clientChannel, response);
				break;
			case GUESS:
				response = this.controller.makeGuess(clientMessage.body);
				this.server.respondToClient(clientChannel, response);
				break;
			case DISCONNECT:
				//See HangmanServer.readFromClient
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * Called whenever a message is received from the client and needs to be
	 * read. Clears the <code>ByteBuffer</code> before filling with the read
	 * bytes, then the message is extracted from the read bytes and added to
	 * the <code>MessageSplitter</code>. Finally it runs the ClientHandler.run 
	 * method.
	 * @throws IOException when the connection to the client is lost.
	 */
	void readMessage() throws IOException {
		this.fromClient.clear();
		int numOfReadBytes = this.clientChannel.read(this.fromClient);
		if(numOfReadBytes == -1) {
			throw new IOException("Connection to client closed!");
		}
		String receivedMessage = extractBufferMessage();
		this.messageDivider.addReceivedMessage(receivedMessage);
		//ForkJoinPool.commonPool().execute(this);
		run();
	}
	
	/**
	 * Called to send a <code>ByteBuffer</code> message to the client.
	 * 
	 * @param msg the <code>ByteBuffer</code> message.
	 * @throws IOException if some I/O problem occurs.
	 */
	void sendMessage(ByteBuffer msg) throws IOException {
		this.clientChannel.write(msg);
	}
	
	/**
	 * Called to close the client channel.
	 * @throws IOException if some problem closing the socket occurs.
	 */
	void disconnectClient() throws IOException {
		this.clientChannel.close();
	}
	
	private String extractBufferMessage() {
		this.fromClient.flip();
		byte[] bytes = new byte[this.fromClient.remaining()];
		this.fromClient.get(bytes);
		return new String(bytes);
	}
	
	private class ClientMessage{
		private String msgType;
		private String body = null;
		private String[] splittedMessage;
		
		private ClientMessage(String clientMessage) {
			this.splittedMessage = clientMessage.split(Constants.MSG_TYPE_DELIMITER);
			this.msgType = getParameter(splittedMessage, Constants.MSG_TYPE_INDEX);
			switch (MessageType.valueOf(this.msgType)) {
			case GUESS:
				this.body = getParameter(splittedMessage, Constants.MSG_BODY_INDEX);
				break;
			default:
				break;
			}	
			
		}
		
		private String getParameter(String[] splittedMsg, int index) {
			if(index >= this.splittedMessage.length) return null;
			if(splittedMsg == null) return null;
			return this.splittedMessage[index];
		}
	}
}
