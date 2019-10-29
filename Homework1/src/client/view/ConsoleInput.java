package client.view;

import java.util.Scanner;

import client.net.OutputHandler;

public class ConsoleInput implements Runnable{
	private static final String PROMPT = ">>";
	private final Scanner console = new Scanner(System.in);
	private boolean active = false;
	//CONTROLLER
	//SAFE THREAD PRINTOUT
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
		while(active) {
			CommandLine commandLine = new CommandLine(readNextLine());
			Command currentCommand = commandLine.getCommand();
			switch (currentCommand) {
			case HELP:
				System.out.println("Hej");
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
		//PRINTOUT PROMT AND SCAN N RETURN NEXT LINE
		return this.console.nextLine();
	}
}
