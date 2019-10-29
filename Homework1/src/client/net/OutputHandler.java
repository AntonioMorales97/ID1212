package client.net;

public interface OutputHandler {
	public void handleAnswer(String message);
	
	public void handleMessage(String message);
}
