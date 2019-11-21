package server.integration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import common.FileDTO;
import server.model.File;

/**
 * Holds the connection to the database and handles all the queries 
 * to the database.
 * 
 * @author Antonio
 *
 */
public class CatalogDAO {
	private Connection conn;
	
	/**
	 * Creates an instance of this class and also tries to create a
	 * <code>DBConnection</code> to the database.
	 */
	public CatalogDAO() {
		try {
			this.conn = new DBConnection().getDBConnetion();
		} catch (SQLException e) {
			System.err.println("Failed to connect to database!");
			e.printStackTrace();
		}
	}
	
	/*
	public void printUsers() throws SQLException {
		String query = "SELECT * from user";
		Statement stmt = this.conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next()) {
			System.out.println(rs.getString("username") + " : " + rs.getString("password"));
		}
	}
	
	public void printFiles() throws SQLException {
		String query = "SELECT * from file";
		Statement stmt = this.conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next()) {
			System.out.println(rs.getString("owner") + " : " + rs.getString("name") + 
					" : " + rs.getLong("size") + " : " + rs.getBoolean("writable"));
		}
	}
	*/
	
	/**
	 * Checks if the given credentials are valid.
	 * 
	 * @param username the username.
	 * @param password the password.
	 * @return <code>true</code> if valid; <code>false</code> otherwise.
	 */
	public boolean validCredentials(String username, String password){
		String query = "SELECT username FROM user WHERE username = ? AND password = ?";
		PreparedStatement prepStatment;
		try {
			prepStatment = this.conn.prepareStatement(query);
			prepStatment.setString(1, username);
			prepStatment.setString(2, password);
			ResultSet rs = prepStatment.executeQuery();
			if(rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			return false;
		}
		
	}
	
	/**
	 * Tries to register a user with the given credentials.
	 * 
	 * @param username the username.
	 * @param password the password.
	 * @return <code>true</code> if registration was successful; <code>false</code>
	 * otherwise.
	 */
	public boolean registerUser(String username, String password) {
		String query = "INSERT INTO user (username, password) VALUES (?, ?)";
		PreparedStatement prepStatement;
		try {
			prepStatement = this.conn.prepareStatement(query);
			prepStatement.setString(1, username);
			prepStatement.setString(2, password);
			int res = prepStatement.executeUpdate();
			prepStatement.close();
			this.conn.commit();
			if(res == 1) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Checks if the given file has the given owner as it's owner.
	 * 
	 * @param username the username to check.
	 * @param file the filename.
	 * @return <code>true</code> if the username match the file's owner; <code>false</code> otherwise.
	 * @throws SQLException if something goes wrong with the database access.
	 */
	/*
	public boolean isFileOwner(String username, String file) throws SQLException {
		String query = "SELECT name FROM file WHERE owner = ? AND name = ?";
		PreparedStatement prepStatment = this.conn.prepareStatement(query);
		prepStatment.setString(1, username);
		prepStatment.setString(2, file);
		ResultSet rs = prepStatment.executeQuery();
		return rs.next();
	}
	*/
	
	/**
	 * Checks the file's permission; i.e if it is writable or not (possible to
	 * delete or update).
	 * 
	 * @param file the filename
	 * @return <code>true</code> if it is writable; <code>false</code> otherwise.
	 * @throws SQLException if something goes wrong with the database access.
	 */
	/*
	public boolean isFileWritable(String file) throws SQLException {
		String query = "SELECT name FROM file WHERE name = ? AND writable = true";
		PreparedStatement prepStatement = this.conn.prepareStatement(query);
		prepStatement.setString(1, file);
		ResultSet rs = prepStatement.executeQuery();
		return rs.next();
	}
	*/
	
	/**
	 * Adds metadata of the file to the database.
	 * 
	 * Note that the actual content of the file is not stored in the database.
	 * 
	 * @param owner the username of the owner of the file.
	 * @param file the file name.
	 * @param size the size of the file.
	 * @param writePermission if all the user have write permission or not.
	 * @return <code>true</code> if insertion was successful; <code>false</code> otherwise.
	 */
	public boolean addFile(String owner, String file, long size, boolean writePermission){
		String query = "INSERT INTO file (owner, name, size, writable) VALUES (?, ?, ?, ?)";
		PreparedStatement prepStatement;
		try {
			prepStatement = this.conn.prepareStatement(query);
			prepStatement.setString(1, owner);
			prepStatement.setString(2, file);
			prepStatement.setLong(3, size);
			prepStatement.setBoolean(4, writePermission);
			int res = prepStatement.executeUpdate();
			prepStatement.close();
			this.conn.commit();
			if(res == 1) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Deletes the file information from the database if it is the owner that is 
	 * the user deleting it or if the file has writable permission.
	 * 
	 * Note that the actual content of the file is not stored in the database.
	 * 
	 * @param username the username of the user deleting.
	 * @param file the file name.
	 * @return <code>true</code> if deletion was successful; <code>false</code> otherwise.
	 */
	public boolean deleteFile(String username, String file){
		String query = "DELETE FROM file WHERE name = ? AND (owner = ? OR writable = true)";
		PreparedStatement prepStatement;
		try {
			prepStatement = this.conn.prepareStatement(query);
			prepStatement.setString(1, file);
			prepStatement.setString(2, username);
			int res = prepStatement.executeUpdate();
			prepStatement.close();
			this.conn.commit();
			if(res == 1) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			return false;
		}
		
		
	}
	
	/**
	 * Updates a file's information; i.e its size.
	 * 
	 * @param file the filename
	 * @param username the username requesting the update.
	 * @param size the new size of the file
	 * @return <code>true</code> if update was successful; <code>false</code> otherwise.
	 * @throws SQLException if something goes wrong with the database access.
	 */
	public boolean updateFile(String file, String username, long size){
		String query = "UPDATE file SET size = ? WHERE name = ? AND (writable = true OR owner = ?)";
		PreparedStatement prepStatement;
		try {
			prepStatement = this.conn.prepareStatement(query);
			prepStatement.setLong(1, size);
			prepStatement.setString(2, file);
			prepStatement.setString(3, username);
			int res = prepStatement.executeUpdate();
			prepStatement.close();
			this.conn.commit();
			if(res == 1) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			return false;
		}	
	}
	
	/**
	 * Updates all information of a file. Can only be called if the username
	 * is the file's owner.
	 * @param username the user/the owner.
	 * @param file the name of the file.
	 * @param size the size of the file.
	 * @param writable the permission of the file.
	 * @return <code>true</code> if updated; <code>false</code> otherwise.
	 */
	public boolean updateWholeFile(String username, String file, long size, boolean writable) {
		String query = "UPDATE file SET size = ?, writable = ? WHERE owner = ? AND name = ?";
		PreparedStatement prepStatement;
		try {
			prepStatement = this.conn.prepareStatement(query);
			prepStatement.setLong(1, size);
			prepStatement.setBoolean(2, writable);
			prepStatement.setString(3, username);
			prepStatement.setString(4, file);
			int res = prepStatement.executeUpdate();
			prepStatement.close();
			this.conn.commit();
			if(res == 1) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Retrieves the metadata of the file stored in the database as a <code>File</code>;
	 * or <code>null</code> if there is none.
	 * 
	 * @param file the file's name.
	 * @return a <code>File</code> containing the metadata of the file.
	 */
	public File getFileInformation(String file) {
		String query = "SELECT * FROM file WHERE name = ?";
		PreparedStatement prepStatement;
		try {
			prepStatement = this.conn.prepareStatement(query);
			prepStatement.setString(1, file);
			ResultSet rs = prepStatement.executeQuery();
			if(rs.next()) {
				return new File(rs.getString("owner"), rs.getString("name"),
						rs.getLong("size"), rs.getBoolean("writable"));
			} else {
				return null;
			}
		} catch (SQLException e) {
			return null;
		}
	}
	
	/**
	 * Lists all the files in the database with their information.
	 * 
	 * @return an <code>ArrayList</code> of <code>File</code>s or <code>null</code> if it's empty.
	 */
	public ArrayList<FileDTO> getFiles(){
		String query = "SELECT * FROM file";
		Statement stmt;
		try {
			stmt = this.conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ArrayList<FileDTO> list = new ArrayList<>();
			while(rs.next()) {
				list.add(new File(rs.getString("owner"), rs.getString("name"), rs.getLong("size"), rs.getBoolean("writable")));
			}
			if(list.isEmpty()) {
				return null;
			} else {
				return list;
			}
		} catch (SQLException e) {
			return null;
		}	
	}
	
	/*
	public static void main(String[] args) throws SQLException, InterruptedException {
		CatalogDAO catalog = new CatalogDAO();
		catalog.printFiles();
		System.out.println("Sleeping");
		Thread.sleep(2000);
		boolean success = catalog.addFile("janedoe", "file1.txt", 10, false);
		catalog.printFiles();
		System.out.println(success);
		System.out.println(catalog.registerUser("jonedoe", "hejsan"));
		catalog.printUsers();
	}
	*/

}
