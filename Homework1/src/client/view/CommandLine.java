package client.view;

import common.Constants;

public class CommandLine {
	private static final String PARAM_DELIMETER = " ";
	private final String commandLine;
	private String[] parameters;
	private Command command;
	private String username;
	private String password;
	
	CommandLine(String commandLine){
		this.commandLine = commandLine;
		parseCommand(commandLine);
		configIfLogin();
	}
	
	Command getCommand() {
		return this.command;
	}
	
	String getParameter(int index) {
		if(this.parameters == null) return null;
		if(index >= this.parameters.length) return null;
		
		return this.parameters[index];
	}
	
	String getCommandLine() {
		return this.commandLine;
	}
	
	String getUsername() {
		return this.username;
	}
	
	String getPassword() {
		return this.password;
	}
	
	private void parseCommand(String commandLine) {
		try {
			this.parameters = removeWhiteSpace(commandLine).split(PARAM_DELIMETER);
			this.command = Command.valueOf(this.parameters[Constants.COMMANDLINE_COMMAND_INDEX].toUpperCase());
		} catch(Throwable commandLineFailure) {
			this.command = Command.COMMAND_ERROR;
		}
		
	}
	
	private void configIfLogin() {
		if(this.command.equals(Command.LOGIN)) {
			this.username = getParameter(Constants.COMMANDLINE_USERNAME_INDEX);
			this.password = getParameter(Constants.COMMANDLINE_PASSWORD_INDEX);	
		}
	}
	
	private String removeWhiteSpace(String commandLine) {
		if(commandLine == null) {
			return null;
		}
		return commandLine.trim().replaceAll(PARAM_DELIMETER + "+", PARAM_DELIMETER);
	}
}
