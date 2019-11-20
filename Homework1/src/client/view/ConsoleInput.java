package client.view;

import java.util.Scanner;

import client.controller.Controller;
import client.net.OutputHandler;

/**
 * This class handles the client application, i.e connection to the
 * server and console input with different commands.
 * 
 * @author Antonio
 *
 */
public class ConsoleInput implements Runnable{
	private static final String PROMPT = "$ ";
	private static final int COMMANDLINE_GUESS_INDEX = 1;
	private final Scanner console = new Scanner(System.in);
	private boolean active = false;
	private final Controller controller = new Controller();
	private final SynchronizedStandardOutput out = new SynchronizedStandardOutput();
	private final OutputHandler outputHandler = new ConsoleOutput();
	private String host;
	private int port;

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
				try {
					this.controller.sendGuess(commandLine.getParameter(COMMANDLINE_GUESS_INDEX));
				} catch (Throwable e) {
					this.out.println(e.getMessage());
				}
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
				try {
					this.controller.startGame();
				} catch (Throwable e) {
					this.out.println(e.getMessage());
				}
				break;
			case QUIT:
				try {
					if(this.controller.isConnectedToServer()) {
						this.controller.disconnect();
					}
					this.active = false;
				} catch (Throwable e) {
					this.out.println("Failed to disconnect");
				}
				break;
			case CONNECT:
				this.controller.connect(this.host, this.port, this.outputHandler);
				break;
			case DISCONNECT:
				try {
					this.controller.disconnect();
				} catch(Throwable e) {
					this.out.println("Failed to disconnect");
				}
				break;
			case INFO:
				this.out.println("Hangman game. Start a connection to server and start a new game!");
				break;
			case COMMAND_ERROR:
				this.out.println("Type \"help\" to see commands");
				break;
			case LOGIN:
				String username = commandLine.getUsername();
				String password = commandLine.getPassword();
				if((username != null) && (password != null)) {
					try {
						this.controller.login(username, password);
					} catch (Throwable e) {
						this.out.println(e.getMessage());
					}
				} else {
					this.out.println("Please enter username and password!");
				}
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
