package client.view;

import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import client.net.OutputHandler;
import common.Constants;
import common.FileDTO;
import common.MessageType;
import common.Observer;

/**
 * Handles server and client output and displays it in the correct way to the client
 * (console output). Implements <code>OutputHandler</code>.
 * 
 * @author Antonio
 *
 */
public class ConsoleOutput extends UnicastRemoteObject implements Observer, OutputHandler {
	private static final String FILES_DIR = System.getProperty("user.dir") + "\\src\\client\\resources\\"; 

	private static final String PROMPT = "$ ";
	private final SynchronizedStandardOutput out = new SynchronizedStandardOutput();

	/**
	 * Creates an instance of this class that extends <code>UnicastRemoteObject</code>.
	 * @throws RemoteException if failed to export the created <code>UnicastRemoteObject</code>
	 */
	public ConsoleOutput() throws RemoteException{
		super();
	}

	/**
	 * Called to notify the client (that holds this instance, i.e the owner) who
	 * updated the client's file.
	 */
	@Override
	public void notifyUpdated(String username, String fileName) {
		this.out.println(username + " updated your file: " + fileName);
		this.out.print(PROMPT);
	}

	/**
	 * Called to notify the client (that holds this instance, i.e the owner) who
	 * deleted the client's file.
	 */
	@Override
	public void notifyDeleted(String username, String fileName) {
		this.out.println(username + " deleted your file: " + fileName);
		this.out.print(PROMPT);
	}

	/**
	 * Called to notify the client (that holds this instance, i.e the owner) who
	 * downloaded the client's file.
	 */
	@Override
	public void notifyDownloaded(String username, String fileName) {
		this.out.println(username + " downloaded your file: " + fileName);
		this.out.print(PROMPT);
	}

	/**
	 * Prints out simple messages with the <code>SynchronizedStandardOutput</code>.
	 */
	@Override
	public void handleMessage(String message) {
		this.out.println(message);
		this.out.print(PROMPT);
	}
	
	/**
	 * Interprets server response and prints it out with the <code>SynchronizedStandardOutput</code>.
	 */
	@Override
	public void handleResponse(String message) {
		MessageParser msgParser = new MessageParser(message);
		MessageType msgType = msgParser.msgType;
		switch (msgType) {
		case UPLOAD_SUCCESS:
			this.out.println("Upload successful!");
			break;
		case DELETE_SUCCESS:
			this.out.println("Delete successful!");
			break;
		case DOWNLOAD_RESPONSE:
			try {
				receiveFile(msgParser.fileName, msgParser.fileContent);
				this.out.println(msgParser.fileName + " downloaded!");
			} catch (IOException e) {
				this.out.println("Something went wrong when trying to receive file!");
			}
			break;
		default:
			break;
		}
		this.out.print(PROMPT);
	}

	/**
	 * Lists all the commands.
	 */
	void printHelp() {
		this.out.println("REGISTER username password\n" +
				"LOGIN username password\n"
				+ "LOGOUT\n"
				+ "DOWNLOAD file\n"
				+ "UPLOAD file writable\n"
				+ "UPDATE file\n"
				+ "DELETE file\n"
				+ "LIST\n"
				+ "QUIT\n"
				+ "HELP");
	}

	/**
	 * Prints that the user is already registered.
	 */
	void printAlreadyRegistered() {
		this.out.println("The username is already registered!");
	}

	/**
	 * Prints a message notifying the client if the registration was
	 * successful or not depending on the given parameter.
	 * @param successReg <code>true</code> if registration was successful;
	 * <code>false</code> otherwise.
	 */
	void printRegisterStatus(boolean successReg) {
		if(successReg) {
			this.out.println("Registration successful, please login!");
		} else {
			this.out.println("Registration failed, username is probably taken. Try another!");
		}
	}

	/**
	 * Notifies the client that login is needed.
	 */
	void printPleaseLogin() {
		this.out.println("Please login first!");
	}
	
	/**
	 * Notifies the client that the client is already logged in.
	 */
	void printAlreadyLoggedIn() {
		this.out.println("You are already logged in!");
	}

	/**
	 * Prints a message notifying the client if the attempt to login
	 * was successful or not depending on the given parameter.
	 * @param successLogin <code>true</code> if login was successful;
	 * <code>false</code> otherwise.
	 */
	void printLoginStatus(boolean successLogin) {
		if(successLogin) {
			this.out.println("Successful login!");
		} else {
			this.out.println("Failed to login!");
		}
	}

	/**
	 * Notifies the client that the client is already logged out.
	 */
	void printAlreadyLoggedOut() {
		this.out.println("You are already logged out!");
	}

	/**
	 * Notifies the client that the client logged out.
	 */
	void printLoggedOut() {
		this.out.println("Logged out.");
	}

	/**
	 * Notifies the client that a file was uploaded. Note however
	 * that this is called from the client-side as a result of a positive 
	 * response from the <code>Catalog</code>, not from the <code>ServerSocket</code>
	 * in the server-side.
	 */
	void printUpload() {
		this.out.println("Uploaded file!");
	}
	
	/**
	 * Notifies the client that an upload of a file failed.
	 */
	void printUploadFail() {
		this.out.println("Upload failed!");
	}
	
	/**
	 * Notifies the client that a file could not be found in the given path.
	 * @param path the complete path to the file.
	 */
	void printNoSuchFile(String path) {
		this.out.println("Could not find a file in: " + path);
	}
	
	/**
	 * Notifies the client that something went wrong with file I/O. 
	 */
	void printFileError() {
		this.out.println("Something went wrong with I/O operations.");
	}
	
	/**
	 * Notifies the client that an updated file was uploaded. Note 
	 * however that this is the result of the upload of the file metadata,
	 * not the actual file.
	 */
	void printUpdateUpload() {
		this.out.println("Uploaded updated file!");
	}
	
	/**
	 * Notifies the client that it failed to update a file in the server. Could be
	 * because a file has read-only permissions.
	 */
	void printUpdateFail() {
		this.out.println("Could not update file. Check permissions.");
	}
	
	/**
	 * Notifies the client that it failed to delete a file from the server.
	 * Could be because the file has certain permissions.
	 */
	void printDeleteFail() {
		this.out.println("Failed to delete file. Check permissions.");
	}
	
	/**
	 * Prints the metadata of the given files; or that the database is empty.
	 * @param files an <code>ArrayList</code> of <code>FileDTO</code>; or <code>null</code>
	 * if there are no files.
	 */
	void printFiles(ArrayList<FileDTO> files) {
		if(files == null) {
			this.out.println("No files in database!");
			return;
		}
		
		this.out.println(listFileToString(files));
	}
	
	/**
	 * Notifies the client that the file was not found in the database.
	 */
	void printFileNotFound() {
		this.out.println("The file was not found!");
	}
	
	/**
	 * Notifies the client what to do if help is needed.
	 */
	void printCommandError() {
		this.out.println("Type \"help\" to see commands");
	}
	
	/**
	 * Prints a simple prompt.
	 */
	void printPrompt() {
		this.out.print(PROMPT);
	}

	private void receiveFile(String fileName, String fileContent) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(FILES_DIR + fileName);
		byte[] strInBytes = fileContent.getBytes();
		fileOut.write(strInBytes);
		fileOut.close();
	}
	
	private String listFileToString(ArrayList<FileDTO> files) {
		StringBuilder sb = new StringBuilder();
		sb.append("Files in database:");
		files.forEach(file -> {
			sb.append("\nOwner: " + file.getOwner() + " Name: " + file.getName() + " Writable: " + file.isWritable() + " Size: " + file.getSize());
		});
		return sb.toString();
	}

	private class MessageParser{
		private MessageType msgType;

		private String fileName;
		private String fileContent;

		private MessageParser (String message) {
			String[] splittedMessage = message.split(Constants.MSG_DELIMITER);
			this.msgType = MessageType.valueOf(splittedMessage[Constants.MSG_TYPE_INDEX]);
			if(this.msgType.equals(MessageType.DOWNLOAD_RESPONSE)) {
				String[] splittedBody = splittedMessage[Constants.MSG_BODY_INDEX].split(Constants.MSG_BODY_DELMITER);
				if(splittedBody.length == 2) {
					this.fileName = splittedBody[Constants.FILE_NAME_INDEX];
					this.fileContent = splittedBody[Constants.FILE_CONTENT_INDEX];
				}
			}
		}
	}
}
