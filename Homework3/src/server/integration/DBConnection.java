package server.integration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Holds the connection to the database.
 * 
 * @author Antonio
 *
 */
class DBConnection {
	private static final String SQL_URL = "jdbc:mysql:///id1212h3?autoReconnect=true&useSSL=false&serverTimezone=UTC";
	private static final String SQL_USER = "id1212h3";
	private static final String SQL_PASS = "id1212h3";
	private static final String SQL_DRIVER = "com.mysql.cj.jdbc.Driver";
	
	private Connection connection;
	
	/**
	 * Creates a connection to the database.
	 * 
	 * @throws SQLException if failed to access the database.
	 */
	DBConnection() throws SQLException {
		try {
			Class.forName(SQL_DRIVER);
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
		this.connection = DriverManager.getConnection(SQL_URL, SQL_USER, SQL_PASS);
		this.connection.setAutoCommit(false);
	}
	
	/**
	 * @return the connection to the database.
	 */
	Connection getDBConnetion() {
		return this.connection;
	}
}
