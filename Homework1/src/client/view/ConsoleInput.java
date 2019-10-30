package client.view;

import java.util.Scanner;

import client.net.OutputHandler;

public class ConsoleInput implements Runnable{
	private static final String PROMPT = "$ ";
	private final Scanner console = new Scanner(System.in);
	private boolean active = false;
	//CONTROLLER
	private final SynchronizedStandardOutput out = new SynchronizedStandardOutput();
	//private static final OutputHandler handler = new ConsoleOutput();

	public void start() {
		if(this.active) {
			return;
		}
		this.active = true;
		//init contr
		new Thread(this).start();
	}

	@Override
	public void run() {
		System.out.println("Welcome :)");
		while(this.active) {
			CommandLine commandLine = new CommandLine(readNextLine());
			Command currentCommand = commandLine.getCommand();
			switch (currentCommand) {
			case HELP:
				System.out.println("Hej");
				break;
			case QUIT:
				this.active = false;
				System.out.println("Bye bye");
				//disconnect connection
				break;
			case COMMAND_ERROR:
				System.out.println("Type \"help\" to see commands");
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
