package common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Receive byte messages from given input streams.
 * 
 * @author Antonio
 *
 */
public class Receiver {
	/**
	 * Receive all the incoming message bytes.
	 * @param inStream the stream where the bytes will come from.
	 * @return a <code>String</code> of the UTF-8 decoded bytes.
	 * @throws IOException if a problem occurs with I/O.
	 */
	public String receiveMessage(BufferedInputStream inStream) throws IOException {
		int length = inStream.read();
		byte[] bytes = new byte[length];
		boolean endStream = false;
		int count = 0;
		while(!endStream && length != 0) {
			int bytesRead = inStream.read(bytes);
			if(bytesRead != -1) {
				count += bytesRead;
				if(count >= length) {
					endStream = true;
				}
			}
		}
		return new String(bytes, 0, length, StandardCharsets.UTF_8);
	}
}
