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
 * The class <code>HangmanServer</code> will run in a specified port and manage
 * new clients by creating new client sockets and starting a new thread to the
 * {@link ClientHandler}.
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
	 * Starts the <code>HangmanServer</code> by creating a listening <code>ServerSocket</code>
	 * that listens for new connections from clients.
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
	
	void respondToClient(SocketChannel channel, String msg) {
		ByteBuffer completeMessage = convertToByteBuffer(msg);
		synchronized(this.bufferToClient) {
			this.bufferToClient.add(completeMessage);
		}
		channel.keyFor(this.selector).interestOps(SelectionKey.OP_WRITE);
		selector.wakeup();
	}
	
	void disconnect(SocketChannel clientChannel) throws IOException {
		clientChannel.keyFor(this.selector).cancel();
		clientChannel.close();
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
			client.handler.readMessage(key);
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
