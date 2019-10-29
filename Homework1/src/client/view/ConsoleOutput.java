package client.view;

import client.net.OutputHandler;

public class ConsoleOutput implements OutputHandler {
	
	@Override
	public void handleMessage(String message) {
		System.out.println(message);
	}

	@Override
	public void handleAnswer(String message) {
		System.out.println("Hej");
		
	}

}
