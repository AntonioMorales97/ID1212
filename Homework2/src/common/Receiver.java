package common;

import java.io.DataInputStream;
import java.io.IOException;

import java.nio.charset.StandardCharsets;

/**
 * A <code>Receiver</code> follows a "length-value" protocol, meaning
 * it will first receive the length of the incoming byte stream and 
 * then listen until it has received all. See {@link Sender}.
 * 
 * @author Antonio
 *
 */
public class Receiver {

	/**
	 * Receive all the incoming bytes.
	 * @param inStream the stream where the bytes will come from.
	 * @return a <code>String</code> of the UTF-8 decoded bytes.
	 * @throws IOException if a problem occurs with I/O.
	 */
	public String receiveAllBytes(DataInputStream inStream) throws IOException{
		int length = inStream.readInt();

		byte[] message = new byte[length];
		int totalBytesRead = 0;
		boolean endStream = false;
		StringBuilder sb = new StringBuilder(length);

		while (!endStream && length != 0) {
			int bytesRead = inStream.read(message);
			if(bytesRead != -1) {
				totalBytesRead += bytesRead;
				if(totalBytesRead <= length) {
					sb.append(new String(message, 0, bytesRead, StandardCharsets.UTF_8));
				} else {
					//Fill to limit, don't overflow
					sb.append(new String(message, 0, length - totalBytesRead + bytesRead, StandardCharsets.UTF_8));
				}
				if(sb.length() >= length) {
					endStream = true;
				}
			}

		}


		return sb.toString();
	}
}
