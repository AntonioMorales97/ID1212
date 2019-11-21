package common;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Send byte messages to given output streams.
 * 
 * @author Antonio
 *
 */
public class Sender {
	
	/**
	 * Sends a message as bytes through the given output stream.
	 * First the number of bytes is sent and the actual bytes.
	 * 
	 * @param message the message to be sent as bytes.
	 * @param out the output stream.
	 */
	public void sendMessage(String message, BufferedOutputStream out) {
		byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
		try {
			out.write((int) bytes.length);
			out.write(bytes);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
