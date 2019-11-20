package server.model;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;

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
	private final Path path;
	private List<String> words;

	/**
	 * Creates a <code>WordReader</code> and reads in the the file with a non-blocking
	 * <code>FileChannel</code> and stores the words in <code>words</code>.
	 * @throws IOException if some error occurs with reading the file.
	 */
	WordReader() throws IOException{
		this.path = Paths.get(WORD_FILE_TOTAL_PATH);
		this.words = readInWordsNonBlocking();
	}

	/**
	 * @return a random word in <code>words</code> as upper case.
	 */
	public String randomWord() {
		Random rand = new Random();
		return this.words.get(rand.nextInt(this.words.size())).toUpperCase();
	}

	private List<String> readInWordsNonBlocking() throws IOException {
		AsynchronousFileChannel asyncChannel = AsynchronousFileChannel.open(this.path, StandardOpenOption.READ);
		ByteBuffer inBuffer = ByteBuffer.allocate(8092*100);
		long position = 0;
		Future<Integer> operation = asyncChannel.read(inBuffer, position);
		while(!operation.isDone());	
		inBuffer.flip();
		byte[] data = new byte[inBuffer.limit()];
		inBuffer.get(data);
		inBuffer.clear();
		return parseReadInWordFile(new String(data));
	}
	
	private List<String> parseReadInWordFile(String wordFile) {
		List<String> words = Arrays.asList(wordFile.split("\\s*\n\\s*"));
		return words;
	}
	
	/*
	public static void main(String[] args) throws IOException, InterruptedException {
		WordReader reader = new WordReader();
		List<String> list = reader.readInWordsNonBlocking();
		for(String w : list) {
			System.out.println("Word: " + w);
		}
		System.out.println("Random word:");
		System.out.println(reader.randomWord());
	}
	*/
	 
}
