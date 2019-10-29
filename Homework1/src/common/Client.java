package common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
/**
 * WILL BE REMOVED: JUST TESTING
 * @author Antonio
 *
 */
public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = new Socket("127.0.0.1", 5000);
		System.out.println("Connected to server");
		DataInputStream in = new DataInputStream(System.in);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        
        int length = 30;
        String data = "This is a string of length 29";
        byte[] dataInBytes = data.getBytes(StandardCharsets.UTF_8);         
        //Sending data in Length Value format        
        out.writeInt(length);
        out.write(dataInBytes);
        while(true);
        //socket.close();
	}

}
