package client.view;

import common.Constants;

/**
 * Represents the read command line; holds the parameters.
 * 
 * @author Antonio
 *
 */
public class CommandLine {
	private static final String PARAM_DELIMETER = " ";
	private final String commandLine;
	private String[] parameters;
	private Command command;

	/**
	 * Creates a <code>CommandLine</code> instance with the given
	 * command line. Also checks if the command line was a login command.
	 * @param commandLine the command line that was read.
	 */
	CommandLine(String commandLine){
		this.commandLine = commandLine;
		parseCommand(commandLine);
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

	private void parseCommand(String commandLine) {
		try {
			this.parameters = removeWhiteSpace(commandLine).split(PARAM_DELIMETER);
			this.command = Command.valueOf(this.parameters[Constants.COMMANDLINE_COMMAND_INDEX].toUpperCase());
		} catch(Throwable commandLineFailure) {
			this.command = Command.COMMAND_ERROR;
		}

	}

	private String removeWhiteSpace(String commandLine) {
		if(commandLine == null) {
			return null;
		}
		return commandLine.trim().replaceAll(PARAM_DELIMETER + "+", PARAM_DELIMETER);
	}
}
