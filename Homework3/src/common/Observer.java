package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The remote methods of an observer.
 * 
 * @author Antonio
 *
 */
public interface Observer extends Remote{
	
	/**
	 * Called to notify the owner that holds the instance implementing this
	 * interface that her file was updated by another user.
	 * @param username the other user that updated the file.
	 * @param fileName the name of the file that was updated.
	 * @throws RemoteException
	 */
	public void notifyUpdated(String username, String fileName) throws RemoteException;
	
	/**
	 * Called to notify the owner that holds the instance implementing this
	 * interface that her file was deleted by another user.
	 * @param username the other user that deleted the file.
	 * @param fileName the name of the file that was deleted.
	 * @throws RemoteException
	 */
	public void notifyDeleted(String username, String fileName) throws RemoteException;
	
	/**
	 * Called to notify the owner that holds the instance implementing this
	 * interface that her file was downloaded by another user.
	 * @param username the other user that downloaded the file.
	 * @param fileName the name of the file that was downloaded.
	 * @throws RemoteException
	 */
	public void notifyDownloaded(String username, String fileName) throws RemoteException;
}
