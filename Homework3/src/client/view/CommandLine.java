package client.view;

/**
 * Represents the read command line; holds the parameters.
 * 
 * @author Antonio
 *
 */
public class CommandLine {
	private static final String PARAM_DELIMITER = " ";
	private static final int COMMANDLINE_COMMAND_INDEX = 0;
	private static final int COMMANDLINE_USERNAME_INDEX = 1;
	private static final int COMMANDLINE_PASSWORD_INDEX = 2;
	public static final int COMMANDLINE_FILENAME_INDEX = 1;
	public static final int COMMANDLINE_FILE_PERMISSION_INDEX = 2;
	private final String commandLine;
	private String[] parameters;
	private Command command;
	private String username;
	private String password;
	
	/**
	 * Creates a <code>CommandLine</code> instance with the given
	 * command line. Also checks if the command line was a login command.
	 * @param commandLine the command line that was read.
	 */
	CommandLine(String commandLine){
		this.commandLine = commandLine;
		parseCommand(commandLine);
		configIfLoginOrRegister();
	}
	
	/**
	 * @return the command
	 */
	Command getCommand() {
		return this.command;
	}
	
	/**
	 * Returns the parameter at the given index.
	 * @param index index of parameter
	 * @return the parameter at the given index as a <code>String</code>; or 
	 * <code>null</code> if it does not exist.
	 */
	String getParameter(int index) {
		if(this.parameters == null) return null;
		if(index >= this.parameters.length) return null;
		
		return this.parameters[index];
	}
	
	/**
	 * @return the command line.
	 */
	String getCommandLine() {
		return this.commandLine;
	}
	
	/**
	 * @return the username
	 */
	String getUsername() {
		return this.username;
	}
	
	/**
	 * @return the password
	 */
	String getPassword() {
		return this.password;
	}
	
	private void parseCommand(String commandLine) {
		try {
			this.parameters = removeWhiteSpace(commandLine).split(PARAM_DELIMITER);
			this.command = Command.valueOf(this.parameters[COMMANDLINE_COMMAND_INDEX].toUpperCase());
		} catch(Throwable commandLineFailure) {
			this.command = Command.COMMAND_ERROR;
		}
		
	}
	
	private void configIfLoginOrRegister() {
		if(this.command.equals(Command.LOGIN) || this.command.equals(Command.REGISTER) ) {
			this.username = getParameter(COMMANDLINE_USERNAME_INDEX);
			this.password = getParameter(COMMANDLINE_PASSWORD_INDEX);	
		}
	}
	
	private String removeWhiteSpace(String commandLine) {
		if(commandLine == null) {
			return null;
		}
		return commandLine.trim().replaceAll(PARAM_DELIMITER + "+", PARAM_DELIMITER);
	}
}
