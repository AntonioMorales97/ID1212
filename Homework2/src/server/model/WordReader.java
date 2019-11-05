package server.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Reads words from a specific word text file for the hangman game.
 * See {@link HangmanGame}.
 * 
 * @author Antonio
 *
 */
public class WordReader {
	private static final String WORD_FILE_PATH = "\\src\\server\\resources\\words.txt";
	private static final String WORD_FILE_TOTAL_PATH = System.getProperty("user.dir") + WordReader.WORD_FILE_PATH;
	private ArrayList<String> wordLines;
	
	/**
	 * Open, reads, and stores the words and picks a random word.
	 * If called again and the words are already read it will not open
	 * the file again.
	 * @return a random word from the word text file.
	 * @throws IOException if some error with I/O occurs.
	 */
	public String randomWord() throws IOException {
		Random rand = new Random();
		if(this.wordLines != null) {
			return this.wordLines.get(rand.nextInt(this.wordLines.size()));
		}
		this.wordLines = new ArrayList<String>();
		File file = new File(WordReader.WORD_FILE_TOTAL_PATH);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String word;
		while((word = reader.readLine()) != null) {
			this.wordLines.add(word.toUpperCase());
		}
		reader.close();
		return this.wordLines.get(rand.nextInt(this.wordLines.size()));
	}
}
