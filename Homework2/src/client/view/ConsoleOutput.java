package client.view;

import client.net.CommunicationListener;
import common.Constants;
import common.MsgType;

/**
 * Handles server output and displays it in the correct way to the client
 * (console output). Implements <code>OutputHandler</code>.
 * 
 * @author Antonio
 *
 */
public class ConsoleOutput implements CommunicationListener {
	private static final String PROMPT = "$ ";
	private final SynchronizedStandardOutput out = new SynchronizedStandardOutput();
	
	@Override
	public void connected() {
		this.out.println("Successfully connected to server!");
		this.out.print(PROMPT);
	}

	@Override
	public void disconnected() {
		this.out.println("Successfully disconnected from server!");
		this.out.print(PROMPT);
	}

	@Override
	public void receivedMessage(String msg) {
		MessageParser msgParser = new MessageParser(msg);
		MsgType msgType = msgParser.msgType;
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
		private MsgType msgType;
		private String message = null;
		
		private MessageParser (String message) {
			String[] splittedMessage = message.split(Constants.MSG_TYPE_DELIMITER);
			this.msgType = MsgType.valueOf(getParameter(splittedMessage, Constants.MSG_TYPE_INDEX_NEW));
			switch (this.msgType) {
			case GAME_RESPONSE:
				this.message = getParameter(splittedMessage, Constants.MSG_BODY_INDEX_NEW);
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
