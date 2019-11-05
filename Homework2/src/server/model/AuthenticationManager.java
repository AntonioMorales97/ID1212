package server.model;

/**
 * Handles authentication. For simplicity, credentials are not
 * encrypted and also hard coded.
 * 
 * @author Antonio
 *
 */
public class AuthenticationManager {
	private static final String USERNAME = "JOHNDOE";
	private static final String PASSWORD = "123456";
	private final JWTHandler jwt = new JWTHandler();
	
	/**
	 * Try to login by checking credentials and generates
	 * a JSON Web Token with <code>JWTHandler</code> if the credentials are
	 * correct.
	 * @param username the username
	 * @param password the password
	 * @return the generated JSON Web Token if successful login.
	 * @throws Throwable if invalid credentials.
	 */
	public String login(String username, String password) throws Throwable {
		if(!isCorrectCredentials(username, password))
			throw new Throwable("Invalid credentials");
		
		return this.jwt.generateJWT(username);
	}
	
	/**
	 * Checks if the JSON Web Token is valid or not.
	 * @param encodedJwt The JSON Web Token
	 * @throws Throwable if invalid.
	 */
	public void validateJwt(String encodedJwt) throws Throwable {
		if(this.jwt.isValid(encodedJwt))
			return;
		throw new Throwable("Please login...");
	}
	
	private boolean isCorrectCredentials(String username, String password) {
		boolean correctUsername = username.toUpperCase().equals(AuthenticationManager.USERNAME);
		boolean correctPassword = password.toUpperCase().equals(AuthenticationManager.PASSWORD);
		return correctUsername && correctPassword ? true : false;
	}
}
