package server.model;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;
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
	private final File wordFile = new File(WORD_FILE_TOTAL_PATH);
	private List<String> words;

	/**
	 * Creates a <code>WordReader</code> and reads in the the file with a non-blocking
	 * <code>FileChannel</code> and stores the words in <code>words</code>.
	 * @throws IOException if some error occurs with reading the file.
	 */
	WordReader() throws IOException{
		this.words = readInWords();
	}

	/**
	 * @return a random word in <code>words</code> as upper case.
	 */
	public String randomWord() {
		Random rand = new Random();
		return this.words.get(rand.nextInt(this.words.size())).toUpperCase();
	}

	private List<String> readInWords() throws IOException {
		StringBuilder sb = new StringBuilder();
		FileInputStream fis = new FileInputStream(this.wordFile);
		FileChannel inChannel = fis.getChannel();
		ByteBuffer inBuffer = ByteBuffer.allocate(8092);
		int c = 0;
		while((c = inChannel.read(inBuffer)) != -1) {
			inBuffer.flip();
			String s = getStringFromBuffer(inBuffer);
			inBuffer.clear();
			sb.append(s);
		}
		fis.close();
		return parseReadInWordFile(sb.toString());
	}

	private String getStringFromBuffer(ByteBuffer buffer) {
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		return new String(bytes);
	}

	private List<String> parseReadInWordFile(String wordFile) {
		List<String> words = Arrays.asList(wordFile.split("\\s*\n\\s*"));
		return words;
	}
	/*
	public static void main(String[] args) throws IOException, InterruptedException {
		WordReader reader = new WordReader();
		List<String> list = reader.readInWords();
		for(String w : list) {
			System.out.println("Word: " + w);
		}
		System.out.println("Random word:");
		System.out.println(reader.randomWord());
	}
	 */
}
