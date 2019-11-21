package server.controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import common.Catalog;
import common.FileDTO;
import common.Observer;
import common.UserDTO;
import server.integration.CatalogDAO;
import server.model.File;
import server.model.User;
import server.model.UserManager;

/**
 * A controller that is also a remote object used by clients.
 * 
 * @author Antonio
 *
 */
public class Controller extends UnicastRemoteObject implements Catalog{
	private UserManager userManager = new UserManager();
	private CatalogDAO catalogDAO;

	public Controller() throws RemoteException {
		super();
		this.catalogDAO = new CatalogDAO();
	}

	/**
	 * Validates username and password. If correct it logs in the user to
	 * the <code>UserManager</code>.
	 * @param username the username.
	 * @param password the password.
	 * @param observer the <code>Observer</code> to notify the user with.
	 * @return the created <code>UserDTO</code>.
	 */
	@Override
	public synchronized UserDTO login(String username, String password, Observer observer) {
		if(this.catalogDAO.validCredentials(username, password)) {
			UserDTO user = new User(username, observer);
			this.userManager.loginUser(user);
			return user;
		}
		return null;
	}

	/**
	 * Log out a user by removing it from the logged in users in the
	 * <code>UserManager</code>.
	 * @param user the user to be logged out.
	 */
	@Override
	public synchronized void logout(UserDTO user) {
		this.userManager.logoutUser(user);
	}

	/**
	 * Tries to register a user.
	 * @param username the username.
	 * @param password the password.
	 * @return <code>true</code> if registered; <code>false</code> otherwise.
	 */
	@Override
	public synchronized boolean registerUser(String username, String password) {
		return this.catalogDAO.registerUser(username, password);
	}

	/**
	 * Tries to upload a file (metadata of a file).
	 * 
	 * @param user the user uploading the file.
	 * @param fileName the name of the file being uploaded.
	 * @param size the size of the file.
	 * @param writable the permissions of the file.
	 * @return <code>true</code> if uploaded; <code>false</code> otherwise.
	 */
	@Override
	public synchronized boolean uploadFile(UserDTO user, String fileName, long size, boolean writable) {
		File file = this.catalogDAO.getFileInformation(fileName);
		if(file == null)
			return catalogDAO.addFile(user.getUsername(), fileName, size, writable);

		if(file.isOwner(user))
			return catalogDAO.updateWholeFile(user.getUsername(), fileName, size, writable);

		return false;
	}

	/**
	 * Tries to update the file metadata and also notify the owner of the file if updated.
	 * 
	 * @param user the user trying to update the file.
	 * @param fileName the file name of the file being updated.
	 * @param size the new size of the file to be updated.
	 * @return <code>true</code> if updated; <code>false</code> otherwise.
	 * @throws RemoteException 
	 */
	@Override
	public synchronized boolean updateFile(UserDTO user, String fileName, long size) throws RemoteException {
		boolean success = this.catalogDAO.updateFile(fileName, user.getUsername(), size);
		if(!success)
			return false;

		File file = this.catalogDAO.getFileInformation(fileName);
		if(file.isOwner(user))
			return success;

		UserDTO owner = this.userManager.getUser(file.getOwner());
		if(owner == null)
			return success;

		owner.getObserver().notifyUpdated(user.getUsername(), fileName);
		return success;
	}

	/**
	 * Tries to delete file and notify the owner of the file if deleted.
	 * 
	 * @param user the user trying to delete the file.
	 * @param fileName the file name of the file to be deleted.
	 * @return <code>true</code> if deleted; <code>false</code> otherwise.
	 * @throws RemoteException 
	 */
	@Override
	public synchronized boolean deleteFile(UserDTO user, String fileName) throws RemoteException {
		File file = this.catalogDAO.getFileInformation(fileName);
		boolean success = this.catalogDAO.deleteFile(user.getUsername(), fileName);
		if(!success)
			return false;

		if(file.isOwner(user))
			return success;

		UserDTO owner = this.userManager.getUser(file.getOwner());
		if(owner == null)
			return success;

		owner.getObserver().notifyDeleted(user.getUsername(), fileName);
		return success;
	}

	/**
	 * Downloads a file (file information) if it exists and tries
	 * to notify the owner who downloaded the file.
	 * 
	 * @param user the user downloading the file.
	 * @param fileName the file being downloaded.
	 * @return the <code>FileDTO</code> containing the file information.
	 * @throws RemoteException 
	 */
	@Override
	public synchronized FileDTO downloadFile(UserDTO user, String fileName) throws RemoteException {
		File file = this.catalogDAO.getFileInformation(fileName);
		if(file == null)
			return null;
		if(file.isOwner(user))
			return file;

		UserDTO owner = this.userManager.getUser(file.getOwner());
		if(owner == null)
			return file;

		owner.getObserver().notifyDownloaded(user.getUsername(), fileName);
		return file;
	}
	
	/**
	 * @return all the files in the database as an <code>ArrayList</code> of 
	 * <code>FileDTO</code>:s; or <code>null</code> if there are no files.
	 */
	@Override
	public synchronized ArrayList<FileDTO> listFiles() throws RemoteException{
		return this.catalogDAO.getFiles();
	}
}
