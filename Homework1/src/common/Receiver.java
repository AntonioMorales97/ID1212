package common;

import java.io.DataInputStream;
import java.io.IOException;

import java.nio.charset.StandardCharsets;

public class Receiver {

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

	/*
	public static void main(String[] args) throws IOException {
		ServerSocket server = new ServerSocket(5000);
		Socket socket = server.accept();
		System.out.println("Got connection from client");
		Receiver r = new Receiver();
		DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		String s = r.receiveAllBytes(in);
		System.out.println(s);
		server.close();
	}
	*/
}
