package client.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringJoiner;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import common.Constants;
import common.MessageDivider;
import common.MessageType;

/**
 * Represents the connection to the server. <code>ServerConnection</code>
 * can connect, disconnect, and send requests from the client to the server.
 * The <code>ServerConnection</code> uses a non-blocking socket, meaning that the 
 * connection will not be blocked when performing operations like connecting,
 * writing, reading, etc.
 * 
 * @author Antonio
 *
 */
public class ServerConnection implements Runnable{
	private static final String FAIL_COMMUNICATION_MSG = "Lost connection.";
	private static final String FAIL_DISCONNECT_MSG = "Failed to disconnect, forcing disconnect...";
	
	private final ByteBuffer receivingBuffer = ByteBuffer.allocateDirect(Constants.MAX_MSG_LENGTH);
	private final Queue<ByteBuffer> sendingBuffer = new ArrayDeque<>();
	private final MessageDivider messageDivider = new MessageDivider();
	private InetSocketAddress serverAddress;
	private Selector selector;
	private SocketChannel socketChannel;
	private boolean connected;
	private volatile boolean timeToSend = false;

	private CommunicationListener listener;

	/**
	 * This is run in a separated thread for the client and represents the
	 * connection to the server. It starts by initializing the connection and configures it 
	 * to be non-blocking; then it opens a <code>Selector</code> to which the <code>SocketChannel</code>
	 * is registered to and set to be ready to connect. The <code>Selector</code> will then block and wait
	 * for channels to be ready for operations, e.g connect, write, read, and act accordingly. 
	 * 
	 * Note however that the connection is non-blocking, meaning that the operations will not block.
	 */
	@Override
	public void run() {
		try {
			initiateConnection();
			initiateSelector();

			while(this.connected || !this.sendingBuffer.isEmpty()) {
				if(this.timeToSend) {
					this.socketChannel.keyFor(this.selector).interestOps(SelectionKey.OP_WRITE);
					this.timeToSend = false;
				}

				this.selector.select();
				for(SelectionKey key : this.selector.selectedKeys()) {
					this.selector.selectedKeys().remove(key);
					if(!key.isValid()) {
						continue;
					}
					if(key.isConnectable()) {
						completeConnection(key);
					} else if(key.isReadable()) {
						readFromServer(key);
					} else if(key.isWritable()) {
						writeToServer(key);
					}
				}
			}
		} catch(Exception exc) {
			notifyMessage(FAIL_COMMUNICATION_MSG);
		}
		try {
			finishDisconnect();
		} catch(IOException exc) {
			notifyMessage(FAIL_DISCONNECT_MSG);
		}

	}

	/**
	 * Sets the server address to connect to and runs in a new thread.
	 * 
	 * @param host The host to connect to (server).
	 * @param port The port number where to connect to.
	 */
	public void connect(String host, int port) {
		this.serverAddress = new InetSocketAddress(host, port);
		new Thread(this).start();
	}
	
	/**
	 * Sets this connection to be disconnected and sends a disconnect message
	 * to the server.
	 */
	public void disconnect() {
		this.connected = false;
		sendMessage(MessageType.DISCONNECT.toString());
	}
	
	/**
	 * Sends a guess message (as part of the game) to the server.
	 * 
	 * @param guessMsg the guess; can be a single letter or a word.
	 */
	public void sendGuess(String guessMsg) {
		if(!this.connected) {
			notifyMessage("Connect and start a game to make guesses!");
			return;
		}
		sendMessage(MessageType.GUESS.toString(), guessMsg);
	}
	
	/**
	 * Sends a start message (as part of the game) to the server, to indicate to
	 * start a new hangman game.
	 */
	public void sendStart() {
		if(!this.connected) {
			notifyMessage("Connect to start a game!");
			return;
		}
		sendMessage(MessageType.START.toString());
	}

	/**
	 * Adds a <code>CommunicationListener</code>. This must be set so the view can be notified.
	 * 
	 * @param listener The <code>CommunicationListener</code>.
	 */
	public void addCommunicationListener(CommunicationListener listener) {
		this.listener = listener;
	}

	private void finishDisconnect() throws IOException {
		this.socketChannel.close();
		this.socketChannel.keyFor(this.selector).cancel();
		notifyDisconnectedComplete();
	}
	
	private void initiateSelector() throws IOException {
		this.selector = Selector.open();
		this.socketChannel.register(selector, SelectionKey.OP_CONNECT);
	}

	private void initiateConnection() throws IOException {
		this.socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel.connect(this.serverAddress);
		this.connected = true;
	}

	private void completeConnection(SelectionKey key) throws IOException {
		this.socketChannel.finishConnect();
		key.interestOps(SelectionKey.OP_READ);
		notifyConnectedComplete();
	}

	private void notifyConnectedComplete() {
		Executor pool = ForkJoinPool.commonPool();
		pool.execute(() -> {
			this.listener.connected();
		});
	}
	
	private void notifyDisconnectedComplete() {
		Executor pool = ForkJoinPool.commonPool();
		pool.execute(() -> {
			listener.disconnected();
		});
	}
	
	private void notifyMessage(String msg) {
		Executor pool = ForkJoinPool.commonPool();
		pool.execute(() -> {
			listener.handleMessage(msg);
		});
	}
	
	private void readFromServer(SelectionKey key) throws IOException {
		this.receivingBuffer.clear();
		int numOfReadBytes = socketChannel.read(this.receivingBuffer);
		if(numOfReadBytes == -1) {
			throw new IOException(FAIL_COMMUNICATION_MSG);
		}
		String readMsg = getMessageFromBuffer();
		this.messageDivider.addReceivedMessage(readMsg);
		while(this.messageDivider.hasNext()) {
			String message = this.messageDivider.nextMessage();
			notifyMessageReceived(message);
		}
	}
	
	private String getMessageFromBuffer() {
		this.receivingBuffer.flip();
		byte[] bytes = new byte[this.receivingBuffer.remaining()];
		this.receivingBuffer.get(bytes);
		return new String(bytes);
	}
	
	private void writeToServer(SelectionKey key) throws IOException {
		ByteBuffer msg;
		synchronized (this.sendingBuffer) {
			while((msg = this.sendingBuffer.peek()) != null) {
				this.socketChannel.write(msg);
				if(msg.hasRemaining()) {
					return;
				}
				this.sendingBuffer.remove();
			}
			key.interestOps(SelectionKey.OP_READ);
		}
	}
	
	private void notifyMessageReceived(String msg) {
		Executor pool = ForkJoinPool.commonPool();
		pool.execute(() -> {
			listener.handleResponse(msg);
		});
	}
	
	private void sendMessage(String... parts) {
		StringJoiner joiner = new StringJoiner(Constants.MSG_TYPE_DELIMITER);
		for(String part : parts) {
			joiner.add(part);
		}
		String msgWithLengthHeader = MessageDivider.addLengthHeader(joiner.toString());
		synchronized(this.sendingBuffer) {
			this.sendingBuffer.add(ByteBuffer.wrap(msgWithLengthHeader.getBytes()));
		}
		this.timeToSend = true;
		this.selector.wakeup();
	}

}
