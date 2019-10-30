package client.startup;

import client.view.ConsoleInput;

public class ClientStarter {

	public static void main(String[] args) {
		System.out.println("Starting Client...");
		new ConsoleInput().start();
	}

}
