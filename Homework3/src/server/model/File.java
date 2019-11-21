package server.model;

import common.FileDTO;
import common.UserDTO;

/**
 * Holds the metadata of a file.
 * 
 * @author Antonio
 *
 */
public class File implements FileDTO{
	private String owner;
	private String name;
	private long size;
	private boolean isWritable;
	
	/**
	 * Creates an instance of this class with the given metadata.
	 * 
	 * @param username the username of the owner of this file.
	 * @param name the name of the file.
	 * @param size the size of the file.
	 * @param isWritable if the file has write (and delete) permissions.
	 */
	public File(String username, String name, long size, boolean isWritable) {
		this.owner = username;
		this.name = name;
		this.size = size;
		this.isWritable = isWritable;
	}

	/**
	 * Checks if the given user is the owner of this file.
	 * @param user the given user.
	 * @return <code>true</code> if the given user is the owner; <code>false</code>
	 * otherwise.
	 */
	@Override
	public boolean isOwner(UserDTO user) {
		if(user.getUsername().equals(this.owner)) {
			return true;
		}
		return false;
	}

	/**
	 * @return <code>true</code> if this file has write (and delete) permissions;
	 * <code>false</code> otherwise.
	 */
	@Override
	public boolean isWritable() {
		return this.isWritable;
	}
	
	/**
	 * @return the name of this file.
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * @return the size of this file.
	 */
	@Override
	public long getSize() {
		return this.size;
	}

	/**
	 * @return the username of the owner of this file.
	 */
	@Override
	public String getOwner() {
		return this.owner;
	}
}
