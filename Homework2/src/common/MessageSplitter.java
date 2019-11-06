package common;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringJoiner;

/**
 * Used for receiving partial messages, adding length headers, adding message type headers,
 * and extracting the message type and the message body.
 * @author Antonio
 *
 */
public class MessageSplitter {
	private StringBuilder receivedChars = new StringBuilder();
	private final Queue<String> messages = new ArrayDeque<>();
	
	/**
	 * Add a received message to the other received messages.
	 * 
	 * @param receivedString The received message.
	 */
	public synchronized void appendReceivedString(String receivedString) {
		this.receivedChars.append(receivedString);
		while(extractCompleteMessage());
	}
	
	/**
	 * @return the first message in the <code>messages</code> queue and then
	 * removes it; or <code>null</code> if it is empty.
	 */
	public synchronized String nextMessage() {
		return this.messages.poll();
	}
	
	/**
	 * @return <code>true</code> if there are messages; <code>false</code> otherwise.
	 */
	public synchronized boolean hasNext() {
		return !this.messages.isEmpty();
	}
	
	/**
	 * Adds a length header to the message.
	 * 
	 * @param msg the message.
	 * @return the message but now with an added length header.
	 */
	public static String addLengthHeader(String msg) {
		StringJoiner joiner = new StringJoiner(Constants.MSG_LEN_DELIMITER);
		joiner.add(Integer.toString(msg.length()));
		joiner.add(msg);
		return joiner.toString();
	}
	
	/**
	 * Checks the type of <code>MsgType</code> of the message.
	 * @param message the message.
	 * @return the <code>MsgType</code> of the given message.
	 */
	public static MsgType typeOfMessage(String message) {
		String[] messageParts = message.split(Constants.MSG_TYPE_DELIMITER);
		return MsgType.valueOf(messageParts[Constants.MSG_TYPE_INDEX].toUpperCase());
	}
	
	/**
	 * @param message the given message.
	 * @return the body of the given message.
	 */
	public static String bodyOfMessage(String message) {
		String[] messageParts = message.split(Constants.MSG_TYPE_DELIMITER);
		return messageParts[Constants.MSG_BODY_INDEX_NEW];
	}
	
	/**
	 * Adds a <code>MsgType</code> header to the message.
	 * @param type the message type to be added.
	 * @param msg the message.
	 * @return a message with the added <code>MsgType</code>.
	 */
	public static String addMsgTypeHeader(String type, String msg) {
		StringJoiner joiner = new StringJoiner(Constants.MSG_TYPE_DELIMITER);
		joiner.add(type);
		joiner.add(msg);
		return joiner.toString();
	}
	
	private boolean extractCompleteMessage() {
		String allReceivedChars = this.receivedChars.toString();
		String[] splitAtLengthHeader = allReceivedChars.split(Constants.MSG_LEN_DELIMITER);
		if(splitAtLengthHeader.length < 2) {
			return false;
		}
		String lengthHeader = splitAtLengthHeader[Constants.MSG_LEN_INDEX];
		int length = Integer.parseInt(lengthHeader);
		if(isComplete(length, splitAtLengthHeader[1])) {
			String completeMessage = splitAtLengthHeader[1].substring(0, length);
			this.messages.add(completeMessage);
			this.receivedChars.delete(0, lengthHeader.length() + Constants.MSG_LEN_DELIMITER.length() + length);
			return true;
		}
		return false;
	}
	
	private boolean isComplete(int messageLength, String receivedMessage) {
		return receivedMessage.length() >= messageLength;
	}
}
