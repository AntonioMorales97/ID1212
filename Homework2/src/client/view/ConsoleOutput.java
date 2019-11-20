package client.view;

import client.net.CommunicationListener;
import common.Constants;
import common.MessageType;

/**
 * Handles server output or other connection related messages and displays it in the correct way to the client
 * (console output). Implements <code>CommunicationListener</code>.
 * 
 * @author Antonio
 *
 */
public class ConsoleOutput implements CommunicationListener {
	private static final String PROMPT = "$ ";
	private final SynchronizedStandardOutput out = new SynchronizedStandardOutput();
	
	/**
	 * Called when connection to the server was successful.
	 */
	@Override
	public void connected() {
		this.out.println("Successfully connected to server!");
		this.out.print(PROMPT);
	}

	/**
	 * Called when disconnected from the server.
	 */
	@Override
	public void disconnected() {
		this.out.println("Disconnected from server!");
		this.out.print(PROMPT);
	}
	
	/**
	 * Called when a message needs to be displayed to the client.
	 */
	@Override
	public void handleMessage(String msg) {
		this.out.println(msg);
		this.out.print(PROMPT);
	}

	/**
	 * Called when a message was received from the server and 
	 * is parsed to be correctly displayed to the client.
	 */
	@Override
	public void handleResponse(String msg) {
		MessageParser msgParser = new MessageParser(msg);
		MessageType msgType = msgParser.msgType;
		switch (msgType) {
		case GAME_RESPONSE:
			String[] gameBody = msgParser.message.split(Constants.MSG_BODY_DELIMETER);
			String gameWord = gameBody[Constants.MSG_BODY_GAME_WORD_INDEX];
			String remainingAttempts = gameBody[Constants.MSG_BODY_GAME_ATTEMPTS_INDEX];
			String score = gameBody[Constants.MSG_BODY_GAME_SCORE_INDEX];
			this.out.println(gameWord + "Remaining attempts: " + remainingAttempts + " Total score: " + score);
			break;
		default:
			break;
		}
		this.out.print(PROMPT);
	}
	
	private class MessageParser{
		private MessageType msgType;
		private String message = null;
		
		private MessageParser (String message) {
			String[] splittedMessage = message.split(Constants.MSG_TYPE_DELIMITER);
			this.msgType = MessageType.valueOf(getParameter(splittedMessage, Constants.MSG_TYPE_INDEX));
			switch (this.msgType) {
			case GAME_RESPONSE:
				this.message = getParameter(splittedMessage, Constants.MSG_BODY_INDEX);
				break;
			default:
				break;
			}

		}
		
		private String getParameter(String[] strArray, int index) {
			if(strArray == null) return null;
			if(index >= strArray.length) return null;
			return strArray[index];
		}
	}
}
