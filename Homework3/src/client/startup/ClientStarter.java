package client.startup;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

import client.view.ConsoleInput;
import common.Catalog;

/**
 * Starts the client.
 * 
 * @author Antonio
 *
 */
public class ClientStarter {
	private static final int PORT = 5000;
	private static final String LOCALHOST = "127.0.0.1";

	/**
	 * Starts the client by getting a <code>Catalog</code> stub object 
	 * from the registry and creating a <code>ConsoleInput</code> with 
	 * the specified host and port.
	 * @param args not used.
	 */
	public static void main(String[] args) {
		try {
			Catalog catalog = (Catalog) Naming.lookup(Catalog.NAME_IN_REGISTRY);
			new ConsoleInput(catalog, LOCALHOST, PORT).start();
		} catch (IOException | NotBoundException e) {
			System.err.println("Problem occured when starting client!");
			e.printStackTrace();
		}
	}

}
