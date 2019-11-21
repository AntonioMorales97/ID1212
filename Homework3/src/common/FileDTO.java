package common;

import java.io.Serializable;

/**
 * A file data transfer object that holds the metadata of a file.
 *  
 * @author Antonio
 *
 */
public interface FileDTO extends Serializable{
	/**
	 * Checks if the given <code>UserDTO</code> is the owner of the file.
	 * @param user the given user.
	 * @return <code>true</code> if the given user is the owner; <code>false</code> otherwise.
	 */
	public boolean isOwner(UserDTO user);
	
	/**
	 * Checks if the file is writable or not (if everyone can modify and delete it).
	 * @return <code>true</code> if it is; <code>false</code> otherwise.
	 */
	public boolean isWritable();
	
	/**
	 * @return the name of the file.
	 */
	public String getName();
	
	/**
	 * @return the size of the file.
	 */
	public long getSize();
	
	/**
	 * @return the username of the owner.
	 */
	public String getOwner();
}
