package server.controller;

import server.model.HangmanGame;

public class Controller {
	private final HangmanGame game = new HangmanGame();
	
	public String startGame() {
		return this.game.startGame();
	}
	
	public String makeGuess(String guess) {
		return this.game.guess(guess);
	}
}
