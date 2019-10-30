package client.view;

import java.util.Scanner;

import client.controller.Controller;
import client.net.OutputHandler;
import common.Constants;

public class ConsoleInput implements Runnable{
	private static final String PROMPT = "$ ";
	private final Scanner console = new Scanner(System.in);
	private boolean active = false;
	private final Controller controller = new Controller();
	private final SynchronizedStandardOutput out = new SynchronizedStandardOutput();
	private final OutputHandler outputHandler = new ConsoleOutput();
	private String host;
	private int port;

	public ConsoleInput(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void start() {
		if(this.active) {
			return;
		}
		this.active = true;
		new Thread(this).start();
	}

	@Override
	public void run() {
		this.out.println("Welcome player :-)");
		while(this.active) {
			CommandLine commandLine = new CommandLine(readNextLine());
			Command currentCommand = commandLine.getCommand();
			switch (currentCommand) {
			case GUESS:
				try {
					this.controller.sendMessage("GUESS"+Constants.MSG_DELIMITER+commandLine.getParameter(Constants.MSG_BODY_INDEX));
				} catch(Throwable e) {
					this.out.println(e.getMessage());
				}	
				break;
			case HELP:
				this.out.println("START - Start a game\n" +
						"GUESS - Guess a letter or a word\n"
						+ "CONNECT - Connect to game server\n"
						+ "DISCONNECT - Disconnect from server\n"
						+ "QUIT - Quit client\n"
						+ "INFO - See info"
						+ "HELP - See help (this)");
				break;
			case START:
				break;
			case QUIT:
				this.active = false;
				//disconnect connection
				break;
			case CONNECT:
				this.controller.connect(this.host, this.port, this.outputHandler);
				break;
			case DISCONNECT:
				//disconnect connection
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
