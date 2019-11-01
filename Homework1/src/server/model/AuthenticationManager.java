package server.model;

public class AuthenticationManager {
	private static final String USERNAME = "JOHNDOE";
	private static final String PASSWORD = "123456";
	private final JWTHandler jwt = new JWTHandler();
	
	public static void main(String[] args) {
		AuthenticationManager manager = new AuthenticationManager();
		System.out.println(manager.isCorrectCredentials("johndoe", "123456"));
	}
	
	public String login(String username, String password) throws Throwable {
		if(!isCorrectCredentials(username, password))
			throw new Throwable("Invalid Credentials");
		
		return this.jwt.generateJWT(username);
	}
	
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
