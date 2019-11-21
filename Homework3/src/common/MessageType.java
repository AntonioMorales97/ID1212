package common;

/**
 * Different message types that are sent over sockets.
 * 
 * @author Antonio
 *
 */
public enum MessageType {
	/**
	 * Message type for uploading files.
	 */
	UPLOAD,
	
	/**
	 * Message type for downloading files.
	 */
	DOWNLOAD,
	
	/**
	 * Message type for deleting files.
	 */
	DELETE,
	
	/**
	 * Message type for disconnecting clients.
	 */
	DISCONNECT,
	
	/**
	 * Message type for successful upload.
	 */
	UPLOAD_SUCCESS,
	
	/**
	 * Message type for successful deletion.
	 */
	DELETE_SUCCESS,
	
	/**
	 * Message type for a download response.
	 */
	DOWNLOAD_RESPONSE
}
