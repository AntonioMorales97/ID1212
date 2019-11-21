package server.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;

import common.Constants;
import common.MessageType;
import common.Receiver;
import common.Sender;

/**
 * Manages client requests of uploading, downloading, and deleting of files.
 * Note that this is only after the client has received approval from the <code>Catalog</code>.
 * 
 * @author Antonio
 *
 */
class ClientHandler implements Runnable {
	private static final String FILES_DIR = System.getProperty("user.dir") + "\\src\\server\\resources\\"; 
	private boolean isConnected = false;
	private final Socket socket;
	private BufferedOutputStream toClient;
	private BufferedInputStream fromClient;
	private Sender sender = new Sender();
	private Receiver receiver = new Receiver();

	/**
	 * Creates an instance of this class with the given socket to the client.
	 * 
	 * @param clientSocket the <code>Socket</code> to the client.
	 */
	ClientHandler(Socket clientSocket){
		this.socket = clientSocket;
		this.isConnected = true;
	}

	/**
	 * Handles client requests.
	 */
	@Override
	public void run() {
		try {
			this.toClient = new BufferedOutputStream(new DataOutputStream(this.socket.getOutputStream()));
			this.fromClient = new BufferedInputStream(new DataInputStream(this.socket.getInputStream()));

		} catch (IOException e) {
			System.err.println("Failed to set up client streams.");
			e.printStackTrace();
		}
		
		while(this.isConnected) {
			try {
				String message = receiver.receiveMessage(this.fromClient);
				ClientMessage clientMsg = new ClientMessage(message);
				switch (MessageType.valueOf(clientMsg.type)) {
				case UPLOAD:
					FileOutputStream fileOut = new FileOutputStream(FILES_DIR + clientMsg.fileName);
					byte[] strInBytes = clientMsg.fileContent.getBytes();
					fileOut.write(strInBytes);
					fileOut.close();
					this.sender.sendMessage(MessageType.UPLOAD_SUCCESS.toString(), this.toClient);
					break;
				case DOWNLOAD:
					File file = new File(FILES_DIR + clientMsg.body);
					String response = MessageType.DOWNLOAD_RESPONSE.toString() + Constants.MSG_DELIMITER
							+ clientMsg.body + Constants.MSG_BODY_DELMITER
							+ new String(Files.readAllBytes(file.toPath()));
					this.sender.sendMessage(response, this.toClient);
					break;
				case DELETE:
					File fileToDelete = new File(FILES_DIR + clientMsg.body);
					Files.delete(fileToDelete.toPath());
					this.sender.sendMessage(MessageType.DELETE_SUCCESS.toString(), this.toClient);
					break;
				case DISCONNECT:
					disconnect();
					break;
				default:
					break;
				}				
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("Caught IOException... client disconnected!");
				this.isConnected = false;
			}
		}
	}
	
	private void disconnect() {
		try {
			this.socket.close();
			this.isConnected = false;
			System.out.println("Client disconnected");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class ClientMessage{
		private String type;
		private String body;
		private String[] splittedMessage;
		
		private String fileName;
		private String fileContent;
		
		private ClientMessage(String clientMessage) {
			this.splittedMessage = clientMessage.split(Constants.MSG_DELIMITER);
			this.type = this.splittedMessage[Constants.MSG_TYPE_INDEX];
			if(!this.type.equals(MessageType.DISCONNECT.toString())) {
				this.body = this.splittedMessage[Constants.MSG_BODY_INDEX];
			}
			if(MessageType.valueOf(this.type).equals(MessageType.UPLOAD)) {
				setFileNameAndContent();
			}
		}
		
		private void setFileNameAndContent() {
			String[] splittedBody = this.body.split(Constants.MSG_BODY_DELMITER);
			if(splittedBody.length == 2) {
				this.fileName = splittedBody[Constants.FILE_NAME_INDEX];
				this.fileContent = splittedBody[Constants.FILE_CONTENT_INDEX];
			}
		}
		
		
	}
}
