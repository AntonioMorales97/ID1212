package server.model;

import java.io.IOException;
import java.util.Arrays;
//import java.util.HashSet;

/**
 * Hangman game logic.
 * @author Antonio
 *
 */
public class HangmanGame {
	private final WordReader wordReader = new WordReader();
	//private HashSet<Character> guessedLetters = new HashSet<>();
	private volatile char[] currentGuessWord;
	private volatile char[] word;
	private volatile int totalAttempts;
	private volatile int attemptsLeft;
	private volatile int score = 0;
	private volatile boolean decrementNextScore = false;

	/**
	 * Start a a game by setting up the game and checking if previous
	 * round was won. If it was not won, the score decreases.
	 * 
	 * @return the view of a new created Hangman game
	 */
	public String startGame() {
		try {
			setUpGame();
			if(!this.decrementNextScore) {
				this.decrementNextScore = true;
			} else {
				this.score--;
			}
			return printStart();
		} catch(IOException e) {
			e.printStackTrace();
			return "Failed to read word file in server side";
		}
	}

	/**
	 * Makes a guess, either by guessing a letter (single char) or for the
	 * whole word. If the guess is correct, the remaining attempts remain the
	 * same, otherwise it decreases by one. If the remaining attempts reaches
	 * zero the game is lost and the score is decreases by one. If the <code>currentGuessWord</code> 
	 * equals to the <code>word</code>, meaning that the word is guessed correctly, the game is
	 * won and the score is increased by one.
	 * 
	 * It is not possible to guess if the remaining attempts is zero. Then a new game needs to
	 * be started. If a new game is started without finishing a game it is counted as a loss.
	 * 
	 * @param message The guess, it can be a single letter or a word
	 * @return the game status as a <code>String</code>. Including the current guesses, remaining
	 * attempts and the total score. If the game is over it will return the correct word with the
	 * updated status.
	 */
	public String guess(String message) {
		if(this.attemptsLeft == 0) {
			return "Start a new game!";
		}
		if(message == null)
			return "Please enter a letter or a word to guess!";
		boolean successGuess = false;
		if(message.length() == 1) {
			successGuess = guessLetter(message.charAt(0));
		} else {
			 successGuess = guessWord(message);
		}
		
		StringBuilder sb = new StringBuilder();
		if(!successGuess) {
			this.attemptsLeft--;
			if(gameOver()) {
				this.score--;
				this.decrementNextScore = false;
				sb.append("Game Over: ");
				appendCorrectLetters(sb);
				appendGameStatus(sb);
				return sb.toString();
			}
		}
		
		appendGuessedLetters(sb);
		
		if(gameIsWon()) {
			this.attemptsLeft = 0;
			this.score++;
			this.decrementNextScore = false;
			appendGameStatus(sb);	
		} else {
			appendGameStatus(sb);
		}
		return sb.toString();
	}

	private void setUpGame() throws IOException {
		this.word = this.wordReader.randomWord().toCharArray();
		this.totalAttempts = this.word.length;
		this.attemptsLeft = this.totalAttempts;
		this.currentGuessWord = new char[this.word.length];
		Arrays.fill(this.currentGuessWord, '_');
	}

	private boolean guessLetter(char letter) {
		boolean successGuess = false;
		for(int i = 0; i < this.word.length; i++) {
			if(this.word[i] == letter) {
				this.currentGuessWord[i] = this.word[i];
				successGuess = true;
			}
		}
		return successGuess;
	}

	private boolean guessWord(String guessWord) {
		if(Arrays.equals(guessWord.toCharArray(), this.word)) {
			this.currentGuessWord = this.word;
			return true;
		} else {
			return false;
		}
	}
	
	private boolean gameIsWon() {
		return Arrays.equals(this.currentGuessWord, this.word);
	}
	
	private boolean gameOver() {
		return this.attemptsLeft == 0;
	}
	
	private void appendGuessedLetters(StringBuilder sb) {
		for(char c : this.currentGuessWord) {
			sb.append(c + " ");
		}
	}
	
	private void appendCorrectLetters(StringBuilder sb) {
		for(char c : this.word) {
			sb.append(c + " ");
		}
	}

	private String printStart() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < this.word.length; i++) {
			sb.append("_ ");
		}
		appendGameStatus(sb);
		return sb.toString();
	}

	private void appendGameStatus(StringBuilder sb) {
		sb.append("Remaining attempts: " + this.attemptsLeft);
		sb.append(" Score: " + this.score);
	}
}
