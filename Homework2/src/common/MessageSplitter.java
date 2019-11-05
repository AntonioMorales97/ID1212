package common;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.StringJoiner;

public class MessageSplitter {
	private StringBuilder receivedChars = new StringBuilder();
	private final Queue<String> messages = new ArrayDeque<>();
	
	public synchronized void appendReceivedString(String receivedString) {
		this.receivedChars.append(receivedString);
		while(extractCompleteMessage());
	}
	
	public synchronized String nextMessage() {
		return this.messages.poll();
	}
	
	public synchronized boolean hasNext() {
		return !this.messages.isEmpty();
	}
	
	public static String addLengthHeader(String msg) {
		StringJoiner joiner = new StringJoiner(Constants.MSG_LEN_DELIMITER);
		joiner.add(Integer.toString(msg.length()));
		joiner.add(msg);
		return joiner.toString();
	}
	
	public static MsgType typeOfMessage(String message) {
		String[] messageParts = message.split(Constants.MSG_TYPE_DELIMITER);
		return MsgType.valueOf(messageParts[Constants.MSG_TYPE_INDEX_NEW].toUpperCase());
	}
	
	public static String bodyOfMessage(String message) {
		String[] messageParts = message.split(Constants.MSG_TYPE_DELIMITER);
		return messageParts[Constants.MSG_BODY_INDEX_NEW];
	}
	
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
