package client.view;

import java.util.Scanner;

import client.net.ServerConnection;
import common.Constants;

/**
 * This class handles the client application, i.e connection to the
 * server and console input with different commands.
 * 
 * @author Antonio
 *
 */
public class ConsoleInput implements Runnable{
	private static final String PROMPT = "$ ";
	private final Scanner console = new Scanner(System.in);
	private boolean active = false;
	private final SynchronizedStandardOutput out = new SynchronizedStandardOutput();
	private String host;
	private int port;
	private ServerConnection serverConn;

	/**
	 * Creates a <code>ConsoleInput</code> instance with given parameters
	 * @param host The host to connect to
	 * @param port The port where the host is running its service
	 */
	public ConsoleInput(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Sets the console input to active and creates a thread
	 * that will manage user input.
	 */
	public void start() {
		if(this.active) {
			return;
		}
		this.active = true;
		this.serverConn = new ServerConnection();
		this.serverConn.addCommunicationListener(new ConsoleOutput());
		new Thread(this).start();
	}

	/**
	 * This thread will handle the user input.
	 */
	@Override
	public void run() {
		this.out.println("Welcome player :-)");
		while(this.active) {
			CommandLine commandLine = new CommandLine(readNextLine());
			Command currentCommand = commandLine.getCommand();
			switch (currentCommand) {
			case GUESS:
				this.serverConn.sendGuess(commandLine.getParameter(Constants.COMMANDLINE_GUESS_INDEX));
				break;
			case HELP:
				this.out.println("START - Start a game\n" +
						"GUESS - Guess a letter or a word\n"
						+ "CONNECT - Connect to game server\n"
						+ "DISCONNECT - Disconnect from server\n"
						+ "QUIT - Quit client\n"
						+ "INFO - See info\n"
						+ "HELP - See help (this)");
				break;
			case START:
				this.serverConn.sendStart();
				break;
			case QUIT:
				this.serverConn.disconnect();
				this.active = false;
				break;
			case CONNECT:
				this.serverConn.connect(this.host, this.port);
				break;
			case DISCONNECT:
				this.serverConn.disconnect();
				break;
			case INFO:
				this.out.println("Hangman game. Start a connection to server and start a new game!");
				break;
			case COMMAND_ERROR:
				this.out.println("Type \"help\" to see commands");
				break;
			default:
				break;
			}
		}

	}

	private String readNextLine() {
		this.out.print(PROMPT);
		return this.console.nextLine();
	}
}
