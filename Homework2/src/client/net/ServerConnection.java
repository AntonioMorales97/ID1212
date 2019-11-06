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
import common.MessageSplitter;
import common.MsgType;

/**
 * Represents the connection to the server. <code>ServerConnection</code>
 * can connect, disconnect, and send requests from the client to the server.
 * When authenticated, it will hold the JSON Web Token. When connected to the
 * server, a new thread is created to listen for output from the server.
 * 
 * @author Antonio
 *
 */
public class ServerConnection implements Runnable{
	private static final String FATAL_COMMUNICATION_MSG = "Lost connection.";
	private static final String FATAL_DISCONNECT_MSG = "Failed to disconnect, forcing disconnect...";
	
	private final ByteBuffer receivingBuffer = ByteBuffer.allocateDirect(Constants.MAX_MSG_LENGTH);
	private final Queue<ByteBuffer> sendingBuffer = new ArrayDeque<>();
	private final MessageSplitter messageSplitter = new MessageSplitter();
	private InetSocketAddress serverAddress;
	private Selector selector;
	private SocketChannel socketChannel;
	private boolean connected;
	private volatile boolean timeToSend = false;


	private CommunicationListener listener;

	@Override
	public void run() {
		try {
			initConnection();
			initSelector();

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
			notifyMessage(FATAL_COMMUNICATION_MSG);
		}
		try {
			finishDisconnect();
		} catch(IOException exc) {
			notifyMessage(FATAL_DISCONNECT_MSG);
		}

	}

	public void connect(String host, int port) {
		this.serverAddress = new InetSocketAddress(host, port);
		new Thread(this).start();
	}
	
	public void disconnect() {
		this.connected = false;
		sendMessage(MsgType.DISCONNECT.toString());
	}
	
	public void sendGuess(String guessMsg) {
		if(!this.connected) {
			notifyMessage("Connect and start a game to make guesses!");
			return;
		}
		sendMessage(MsgType.GUESS.toString(), guessMsg);
	}
	
	public void sendStart() {
		if(!this.connected) {
			notifyMessage("Connect to start a game!");
			return;
		}
		sendMessage(MsgType.START.toString());
	}

	public void addCommunicationListener(CommunicationListener listener) {
		this.listener = listener;
	}

	private void finishDisconnect() throws IOException {
		this.socketChannel.close();
		this.socketChannel.keyFor(this.selector).cancel();
		notifyDisconnected();
	}
	
	private void initSelector() throws IOException {
		this.selector = Selector.open();
		this.socketChannel.register(selector, SelectionKey.OP_CONNECT);
	}

	private void initConnection() throws IOException {
		this.socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel.connect(this.serverAddress);
		this.connected = true;
	}

	private void completeConnection(SelectionKey key) throws IOException {
		this.socketChannel.finishConnect();
		key.interestOps(SelectionKey.OP_READ);
		notifyConnected();
	}

	private void notifyConnected() {
		Executor pool = ForkJoinPool.commonPool();
		pool.execute(() -> {
			this.listener.connected();
		});
	}
	
	private void notifyDisconnected() {
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
			throw new IOException(FATAL_COMMUNICATION_MSG);
		}
		String readMsg = getMessageFromBuffer();
		this.messageSplitter.appendReceivedString(readMsg);
		while(this.messageSplitter.hasNext()) {
			String message = this.messageSplitter.nextMessage();
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
			listener.receivedMessage(msg);
		});
	}
	
	private void sendMessage(String... parts) {
		StringJoiner joiner = new StringJoiner(Constants.MSG_TYPE_DELIMITER);
		for(String part : parts) {
			joiner.add(part);
		}
		String msgWithLengthHeader = MessageSplitter.addLengthHeader(joiner.toString());
		synchronized(this.sendingBuffer) {
			this.sendingBuffer.add(ByteBuffer.wrap(msgWithLengthHeader.getBytes()));
		}
		this.timeToSend = true;
		this.selector.wakeup();
	}

}