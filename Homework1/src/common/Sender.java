package common;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Sender {
	
	/**
	 * Sends a message through the <code>DataOutputStream</code>. OBS Charset 
	 * UTF_8!
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
