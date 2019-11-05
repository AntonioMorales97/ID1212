package client.view;

/**
 * Since threads are used in the client, it is safer to synchronize the standard output
 * between the threads.
 * @author Antonio
 *
 */
public class SynchronizedStandardOutput {

	/**
	 * Prints the given string to standard output stream (<code>System.out</code>)
	 * @param text Text to be displayed in the terminal
	 */
	synchronized void print(String text) {
		System.out.print(text);
	}
	
	/**
	 * Prints the given string to standard output stream (<code>System.out</code>) and
	 * adds a newline in the end.
	 * @param text Text to be displayed in the terminal
	 */
	synchronized void println(String text) {
		System.out.println(text);
	}
}
