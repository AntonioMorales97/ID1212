package server.startup;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import server.controller.Controller;
import server.net.Server;

/**
 * Starts the catalog servant and binds it in the RMI registry.
 * Also starts the server for client sockets for file requesting.
 * @author Antonio
 *
 */
public class ServerStarter {

	public static final int PORT = 5000;
	
	/**
	 * Starts the catalog servant and binds it in the RMI registry.
	 * Also starts the server for managing client sockets for file uploading,
	 * downloading, and deletion.
	 * 
	 * @param args not used.
	 */
	public static void main(String[] args) {
		try {
			ServerStarter serverStarter = new ServerStarter();
			serverStarter.startRMIRegistry();
			Naming.rebind(Controller.NAME_IN_REGISTRY, new Controller());
			Server server = new Server(PORT);
			server.start();
		} catch (MalformedURLException | RemoteException exc) {
			exc.printStackTrace();
			System.err.println("Failed to start server!");
		}
	}
	
	private void startRMIRegistry() throws RemoteException {
		try {
			LocateRegistry.getRegistry().list();
		} catch (RemoteException e) {
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		}
	}
}
