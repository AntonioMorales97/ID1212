package server.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

import common.MessageSplitter;
import common.MsgType;

/**
 * The class <code>HangmanServer</code> will run in a specified port and with a 
 * non-blocking <code>ServerSocketChannel</code> and is also responsible of
 * managing the client nodes with a <code>Selector</code>; i.e all communication passes through here.
 * 
 * @author Antonio
 *
 */
public class HangmanServer {
	private static final int LINGER_TIME = 5000;
	private int port = 8000; //default
	private Selector selector;
	private ServerSocketChannel serverSocketChannel;
	private Queue<ByteBuffer> bufferToClient = new ArrayDeque<>();
	
	
	
	/**
	 * Creates a <code>HangmanServer</code>
	 * @param port The port where the server will be started on.
	 */
	public HangmanServer(int port) {
		this.port = port;
	}
	
	/**
	 * Runs the server. This means that it will first configure itself by creating a
	 * <code>Selector</code> for handling clients nodes, opening a <code>ServerSocketChannel</code>
	 * and configuring it to be non-blocking and started on the specified port number and also
	 * registered with the created <code>Selector</code> to wait for new clients. It will then
	 * live and handle ready <code>SelectionKey</code> accordingly.
	 */
	public void run() {
		try {
			config();
			System.out.println("Server socket on port: " + this.port);
			while(true) {
				this.selector.select();
				Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
				while(iterator.hasNext()) {
					SelectionKey key = iterator.next();
					iterator.remove();
					if(!key.isValid()) {
						continue;
					}
					if(key.isAcceptable()) {
						System.out.println("Accept");
						startClientHandler(key);
					} else if (key.isReadable()) {
						System.out.println("Receive");
						readFromClient(key);
					} else if (key.isWritable()) {
						System.out.println("Write");
						writeToClient(key);
					}
				}
			}
			
		} catch(IOException exc) {
			System.err.println("Caught exception from HangmanServer:");
			exc.printStackTrace();
		}
		
	}
	
	/**
	 * Called from a <code>ClientHandler</code> to tell the server that the 
	 * given message is to be sent to the client. It will then add the message
	 * converted into a <code>ByteBuffer</code> to the <code>bufferToClient</code>
	 * and set the key representing the given channel to a write operation. Finally
	 * the <code>Selector</code> is woken up.
	 * @param channel the given client channel.
	 * @param msg the given message.
	 */
	void respondToClient(SocketChannel channel, String msg) {
		ByteBuffer completeMessage = convertToByteBuffer(msg);
		synchronized(this.bufferToClient) {
			this.bufferToClient.add(completeMessage);
		}
		channel.keyFor(this.selector).interestOps(SelectionKey.OP_WRITE);
		selector.wakeup();
	}
	
	private ByteBuffer convertToByteBuffer(String msg) {
		msg = MessageSplitter.addMsgTypeHeader(MsgType.GAME_RESPONSE.toString(), msg);
		String msgWithLengthHeader = MessageSplitter.addLengthHeader(msg);
		return ByteBuffer.wrap(msgWithLengthHeader.getBytes());
	}
	
	private void config() throws IOException {
		this.selector = Selector.open();
		this.serverSocketChannel = ServerSocketChannel.open();
		this.serverSocketChannel.configureBlocking(false);
		this.serverSocketChannel.bind(new InetSocketAddress(this.port));
		this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
	}
	
	private void startClientHandler(SelectionKey key) throws IOException {
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
		SocketChannel clientChannel = serverChannel.accept();
		clientChannel.configureBlocking(false);
		ClientHandler clientHandler = new ClientHandler(this, clientChannel);
		clientChannel.register(this.selector, SelectionKey.OP_WRITE, new HangmanClient(clientHandler));
		clientChannel.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME);
	}
	
	private void readFromClient(SelectionKey key) throws IOException {
		HangmanClient client = (HangmanClient) key.attachment();
		try {
			client.handler.readMessage();
		} catch (IOException clientHasLeft) {
			removeClient(key);
		}
	}
	
	private void writeToClient(SelectionKey key) throws IOException {
		HangmanClient client = (HangmanClient) key.attachment();
		try {
			client.send();
			key.interestOps(SelectionKey.OP_READ);
		} catch (IOException e) {
			e.printStackTrace();
			removeClient(key);
		}
		
	}
	
	private void removeClient(SelectionKey key) throws IOException {
		HangmanClient client = (HangmanClient) key.attachment();
		client.handler.disconnectClient();
		key.cancel();
	}
	
	
	private class HangmanClient{
		private final ClientHandler handler;
		
		private HangmanClient(ClientHandler handler) {
			this.handler = handler;
		}
		
		private void send() throws IOException {
			ByteBuffer msg = null;
			synchronized (bufferToClient) {
				while((msg = bufferToClient.peek()) != null) {
					this.handler.sendMessage(msg);
					bufferToClient.remove();
				}
			}
		}
	}
		
}
