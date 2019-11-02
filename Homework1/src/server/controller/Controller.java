package server.controller;

import server.model.AuthenticationManager;
import server.model.HangmanGame;

/**
 * A simple controller that handles calls from the net-layer to the model-layer.
 * Holds a <code>HangmanGame</code> for the game and a <code>AuthenticationManager</code>
 * for authentication of the client.
 * 
 * @author Antonio
 *
 */
public class Controller {
	private final HangmanGame game = new HangmanGame();
	private AuthenticationManager authManager = new AuthenticationManager();
	
	/**
	 * Start a game in <code>HangmanGame</code>.
	 * @return the game respond to send to the client.
	 */
	public String startGame() {
		return this.game.startGame();
	}
	
	/**
	 * Makes a guess in the <code>HangmanGame</code>.
	 * @param guess the guess
	 * @return the game respond to send to the client.
	 */
	public String makeGuess(String guess) {
		return this.game.guess(guess);
	}
	
	/**
	 * Try to login with <code>AuthenticationManager</code>.
	 * @param username the username
	 * @param password the password
	 * @return the respond
	 * @throws Throwable if invalid credentials.
	 */
	public String login(String username, String password) throws Throwable {
		return this.authManager.login(username, password);
	}
	
	/**
	 * Validate an encoded JSON Web Token.
	 * @param encodedJwt the encoded JSON Web Token
	 * @throws Throwable if JSON Web Token is invalid somehow.
	 */
	public void validJwt(String encodedJwt) throws Throwable {
		this.authManager.validateJwt(encodedJwt);
	}
}
