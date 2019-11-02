package common;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * A <code>Sender</code> follows a "length-value" protocol, meaning it will
 * first send the length of the bytes to send, and then send those bytes.
 * See {@link Receiver}.
 * @author Antonio
 *
 */
public class Sender {
	
	/**
	 * Sends a message through the <code>DataOutputStream</code> as UTF-8 
	 * encoded bytes.
	 *
	 * @param message The message to be sent
	 * @param out The <code>DataOutputStream</code>
	 */
	public void sendMessageAsBytes(String message, DataOutputStream out) {
		byte[] byteMessage = message.getBytes(StandardCharsets.UTF_8);
		try {
			out.writeInt((int) byteMessage.length);
			out.write(byteMessage);
			out.flush();
		} catch(IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
