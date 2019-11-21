package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * The remote methods of the catalog.
 * 
 * @author Antonio
 *
 */
public interface Catalog extends Remote{
	public static final String NAME_IN_REGISTRY = "CATALOG";
	
	/**
	 * Called when a client wants to login.
	 * @param username the username.
	 * @param password the password.
	 * @param observer the <code>Observer</code> of the client so the client can
	 * be notified from the server-side.
	 * @return a <code>UserDTO</code> if successful login; <code>null</code> otherwise.
	 * @throws RemoteException
	 */
	public UserDTO login(String username, String password, Observer observer) throws RemoteException;
	
	/**
	 * Called when a client wants to logout.
	 * @param user the client's <code>UserDTO</code>.
	 * @throws RemoteException
	 */
	public void logout(UserDTO user) throws RemoteException;
	
	/**
	 * Called when a client wants to register.
	 * @param username the username.
	 * @param password the password.
	 * @return <code>true</code> if registration was successful; <code>false</code> otherwise.
	 * @throws RemoteException
	 */
	public boolean registerUser(String username, String password) throws RemoteException;
	
	/**
	 * Called when a client wants to upload a file (meta-data of the file to the server).
	 * @param user the client's <code>UserDTO</code>.
	 * @param fileName the name of the file.
	 * @param size the size of the file.
	 * @param writable <code>true</code> if the file can be modified by everyone; <code>false</code>
	 * otherwise.
	 * @return <code>true</code> if the upload was successful; <code>false</code> otherwise.
	 * @throws RemoteException
	 */
	public boolean uploadFile(UserDTO user, String fileName, long size, boolean writable) throws RemoteException;
	
	/**
	 * Called when a client wants to update a file in the server-side.
	 * @param user the client's <code>UserDTO</code>
	 * @param fileName the name of the file.
	 * @param size the size of the file. 
	 * @return <code>true</code> if the update was successful; <code>false</code> otherwise.
	 * @throws RemoteException
	 */
	public boolean updateFile(UserDTO user, String fileName, long size) throws RemoteException;
	
	/**
	 * Called when a client wants to delete a file from the server-side.
	 * @param user the client's <code>UserDTO</code>.
	 * @param fileName the name of the file.
	 * @return <code>true</code> if deletion was successful; <code>false</code> otherwise.
	 * @throws RemoteException
	 */
	public boolean deleteFile(UserDTO user, String fileName) throws RemoteException;
	
	/**
	 * Called when a client wants to download a file.
	 * @param user the client's <code>UserDTO</code>.
	 * @param fileName the name of the file.
	 * @return a <code>FileDTO</code> containing file information. Note that the actual
	 * file is not downloaded here.
	 * @throws RemoteException
	 */
	public FileDTO downloadFile(UserDTO user, String fileName) throws RemoteException;
	
	/**
	 * Called when a client wants to list all the file in the server's database.
	 * @return an <code>ArrayList</code> of <code>FileDTO</code>:s, or <code>null</code> of
	 * there are no files.
	 * @throws RemoteException
	 */
	public ArrayList<FileDTO> listFiles() throws RemoteException;
}
