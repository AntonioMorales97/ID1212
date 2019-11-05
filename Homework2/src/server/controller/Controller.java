package server.controller;

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
	
}
