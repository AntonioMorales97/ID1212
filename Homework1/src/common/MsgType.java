package common;

/**
 * Different message types.
 * 
 * @author Antonio
 *
 */
public enum MsgType {
	/**
	 * Guess message type
	 */
	GUESS,
	
	/**
	 * Start game message type
	 */
	START,
	
	/**
	 * Disconnect message type
	 */
	DISCONNECT,
	
	/**
	 * Login message type
	 */
	LOGIN,
	
	/**
	 * Game respond message type
	 */
	GAME_RESPOND,
	
	/**
	 * Login success message type
	 */
	LOGIN_SUCCESS,
	
	/**
	 * Login fail message type
	 */
	LOGIN_FAIL,
	
	/**
	 * Invalid request message type
	 */
	INVALID_REQUEST
}
