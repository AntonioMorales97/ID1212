package server.controller;

import server.model.AuthenticationManager;
import server.model.HangmanGame;

public class Controller {
	private final HangmanGame game = new HangmanGame();
	private AuthenticationManager authManager = new AuthenticationManager();
	
	public String startGame() {
		return this.game.startGame();
	}
	
	public String makeGuess(String guess) {
		return this.game.guess(guess);
	}
	
	public String login(String username, String password) throws Throwable {
		return this.authManager.login(username, password);
	}
	
	public void validJwt(String encodedJwt) throws Throwable {
		this.authManager.validateJwt(encodedJwt);
	}
}
