package client.view;

import client.net.OutputHandler;

public class ConsoleOutput implements OutputHandler {
	private static final String PROMPT = "$ ";
	private final SynchronizedStandardOutput out = new SynchronizedStandardOutput();
	
	@Override
	public void handleMessage(String message) {
		//this.out.println(""); //Print message on a new line
		this.out.println(message);
		this.out.print(PROMPT);
	}

	@Override
	public void handleAnswer(String message) {
		//this.out.println(""); //Print message on a new line
		this.out.println(message);
		this.out.print(PROMPT);
	}

}
