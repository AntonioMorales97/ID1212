package client.view;

import client.net.OutputHandler;
import common.Constants;
import common.MessageType;

/**
 * Handles server output and displays it in the correct way to the client
 * (console output). Implements <code>OutputHandler</code>.
 * 
 * @author Antonio
 *
 */
class ConsoleOutput implements OutputHandler {
	private static final String PROMPT = "$ ";
	private final SynchronizedStandardOutput out = new SynchronizedStandardOutput();
	
	/**
	 * Prints out simple messages with the <code>SynchronizedStandardOutput</code>.
	 */
	@Override
	public void handleMessage(String message) {
		this.out.println(message);
		this.out.print(PROMPT);
	}

	/**
	 * Interprets server response and prints it out with the <code>SynchronizedStandardOutput</code>.
	 */
	@Override
	public void handleResponse(String message) {
		MessageParser msgParser = new MessageParser(message);
		MessageType msgType = msgParser.msgType;
		switch (msgType) {
		case LOGIN_SUCCESS:
			this.out.println(msgParser.message);
			break;
		case LOGIN_FAIL:
			this.out.println(msgParser.message);
			break;
		case INVALID_REQUEST:
			this.out.println(msgParser.message);
			break;
		case GAME_RESPONSE:
			String[] gameBody = msgParser.message.split(Constants.MSG_BODY_DELIMITER);
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
			String[] splittedMessage = message.split(Constants.MSG_DELIMITER);
			this.msgType = MessageType.valueOf(splittedMessage[Constants.MSG_TYPE_INDEX]);
			switch (this.msgType) {
			case LOGIN_SUCCESS:
				this.message = "Server responded with: " + MessageType.LOGIN_SUCCESS;
				break;
			case LOGIN_FAIL:
				this.message = "Server responded with: " + splittedMessage[Constants.MSG_BODY_INDEX];
				break;
			case INVALID_REQUEST:
				this.message = "Server responded with: " + splittedMessage[Constants.MSG_BODY_INDEX];
				break;
			case GAME_RESPONSE:
				this.message = splittedMessage[Constants.MSG_BODY_INDEX];
				break;
			default:
				break;
			}

		}
	}
}
