package server.net;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import common.Constants;
import common.MessageSplitter;
import common.MsgType;
import server.controller.Controller;

/**
 * A <code>ClientHandler</code> which will handle the input -and output streams
 * from and to a client and interpret messages.
 * 
 * @author Antonio
 *
 */
public class ClientHandler {
	private final HangmanServer server;
	private final SocketChannel clientChannel;
	private final ByteBuffer fromClient = ByteBuffer.allocate(Constants.MAX_MSG_LENGTH);
	private final MessageSplitter messageSplitter = new MessageSplitter();
	private final Controller controller = new Controller();
	
	ClientHandler(HangmanServer server, SocketChannel socketChannel){
		this.clientChannel = socketChannel;
		this.server = server;
	}
	
	public void run() {
		while(this.messageSplitter.hasNext()) {
			ClientMessage clientMessage = new ClientMessage(this.messageSplitter.nextMessage());
			String response;
			switch (MsgType.valueOf(clientMessage.msgType)) {
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
	
	void readMessage(SelectionKey key) throws IOException {
		this.fromClient.clear();
		int numOfReadBytes = this.clientChannel.read(this.fromClient);
		if(numOfReadBytes == -1) {
			throw new IOException("Connection to client closed!");
		}
		String receivedMessage = extractBufferMessage();
		this.messageSplitter.appendReceivedString(receivedMessage);
		//ForkJoinPool.commonPool().execute(this);
		run();
	}
	
	void sendMessage(ByteBuffer msg) throws IOException {
		this.clientChannel.write(msg);
	}
	
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
			switch (MsgType.valueOf(this.msgType)) {
			case GUESS:
				this.body = getParameter(splittedMessage, Constants.MSG_BODY_INDEX_NEW);
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
