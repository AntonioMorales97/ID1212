package client.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import client.net.ServerConnection;
import common.Catalog;
import common.FileDTO;
import common.UserDTO;

/**
 * This class handles the client application, i.e connection to the
 * server and console input with different commands.
 * 
 * @author Antonio
 *
 */
public class ConsoleInput implements Runnable{
	private static final String FILES_DIR = System.getProperty("user.dir") + "\\src\\client\\resources\\"; 
	private final Scanner console = new Scanner(System.in);
	private boolean connected = false;
	private final ConsoleOutput consoleOutput = new ConsoleOutput();
	private final ServerConnection connection;
	private Catalog catalog;
	private UserDTO user = null;

	/**
	 * Creates a <code>ConsoleInput</code> instance with given parameters
	 * @param catalog the <code>Catalog</code> stub object (RMI).
	 * @param host The host to connect to
	 * @param port The port where the host is running its service
	 * @throws IOException 
	 */
	public ConsoleInput(Catalog catalog, String host, int port) throws IOException {
		this.catalog = catalog;
		this.connection = new ServerConnection();
		this.connection.connect(host, port, this.consoleOutput);
	}

	/**
	 * Sets the instance to connected state and creates a thread
	 * that will manage user input.
	 */
	public void start() {
		if(this.connected) {
			return;
		}
		this.connected = true;
		new Thread(this).start();
	}

	/**
	 * This thread will handle the user input.
	 */
	@Override
	public void run() {
		try {
			while(this.connected) {
				CommandLine commandLine = new CommandLine(readNextLine());
				Command currentCommand = commandLine.getCommand();
				switch (currentCommand) {
				case HELP:
					this.consoleOutput.printHelp();
					break;
				case REGISTER:
					if(this.user != null) {
						this.consoleOutput.printAlreadyRegistered();
					} else {
						boolean successRegister = this.catalog.registerUser(
								commandLine.getUsername(), commandLine.getPassword());
						this.consoleOutput.printRegisterStatus(successRegister);
					}
					break;
				case LOGIN:
					if(this.user != null) {
						this.consoleOutput.printAlreadyLoggedIn();
					} else {
						this.user = this.catalog.login(commandLine.getUsername(), commandLine.getPassword(), this.consoleOutput);
						this.consoleOutput.printLoginStatus(this.user!=null);
						/*
						if(user != null) {
							this.consoleOutput.printLoginStatus(true);
						} else {
							this.consoleOutput.printLoginStatus(false);
						}
						*/
					}			
					break;
				case LOGOUT:
					if(this.user == null) {
						this.consoleOutput.printAlreadyLoggedOut();
					} else {
						this.catalog.logout(this.user);
						this.user = null;
						this.consoleOutput.printLoggedOut();
					}	
					break;
				case DOWNLOAD:
					if(this.user == null) {
						this.consoleOutput.printPleaseLogin();
					} else {
						FileDTO file = this.catalog.downloadFile(user, commandLine.getParameter(CommandLine.COMMANDLINE_FILENAME_INDEX));
						if(file == null) {
							this.consoleOutput.printFileNotFound();
						} else {
							this.connection.downloadFile(file.getName());
							//this.connection.downloadFile(commandLine.getParameter(CommandLine.COMMANDLINE_FILENAME_INDEX));
						}
					}
					break;
				case UPLOAD:
					if(this.user == null) {
						this.consoleOutput.printPleaseLogin();
					} else {
						File file = new File(FILES_DIR + commandLine.getParameter(
								CommandLine.COMMANDLINE_FILENAME_INDEX));
						if(file.isFile()) {
							boolean uploadSuccess = this.catalog.uploadFile(this.user, file.getName(), file.length(), 
									Boolean.parseBoolean(commandLine.getParameter(CommandLine.COMMANDLINE_FILE_PERMISSION_INDEX)));
							if(uploadSuccess) {
								try {
									this.connection.uploadFile(file.getName(), new String(Files.readAllBytes(file.toPath())));
									this.consoleOutput.printUpload();
								} catch (IOException exc) {
									this.consoleOutput.printFileError();
								}
							} else {
								this.consoleOutput.printUploadFail();
							}
						} else {
							this.consoleOutput.printNoSuchFile(FILES_DIR);
						}
					}
					break;
				case UPDATE:
					if(this.user == null) {
						this.consoleOutput.printPleaseLogin();
					} else {
						File file = new File(FILES_DIR + commandLine.getParameter(
								CommandLine.COMMANDLINE_FILENAME_INDEX));
						if(file.isFile()) {
							boolean updateSuccess = this.catalog.updateFile(this.user, file.getName(), file.length());
							if(updateSuccess) {
								try {
									this.connection.uploadFile(file.getName(), new String(Files.readAllBytes(file.toPath())));
									this.consoleOutput.printUpdateUpload();
								} catch (IOException exc) {
									this.consoleOutput.printFileError();
								}	
							} else {
								this.consoleOutput.printUpdateFail();
							}
						} else {
							this.consoleOutput.printNoSuchFile(FILES_DIR);
						}
					}
					break;
				case DELETE:
					if(this.user == null) {
						this.consoleOutput.printPleaseLogin();
					} else {
						boolean deleteSuccess = this.catalog.deleteFile(this.user, commandLine.getParameter(CommandLine.COMMANDLINE_FILENAME_INDEX));
						if(deleteSuccess) {
							this.connection.deleteFile(commandLine.getParameter(CommandLine.COMMANDLINE_FILENAME_INDEX));
						} else {
							this.consoleOutput.printDeleteFail();
						}
					}
					break;
				case LIST:
					if(this.user == null) {
						this.consoleOutput.printPleaseLogin();
					} else {
						this.consoleOutput.printFiles(this.catalog.listFiles());
					}
					break;
				case QUIT:
					this.connected = false;
					if(this.user != null) {
						this.catalog.logout(this.user);
					}
					this.user = null;
					this.connection.disconnect();
					UnicastRemoteObject.unexportObject(this.consoleOutput, false);
					break;
				case COMMAND_ERROR:
					this.consoleOutput.printCommandError();
					break;
				default:
					break;
				}
			}
		} catch (RemoteException exc) {
			System.err.println("Oh no :(");
			exc.printStackTrace();
		}
	}

	private String readNextLine() {
		this.consoleOutput.printPrompt();
		return this.console.nextLine();
	}
}
