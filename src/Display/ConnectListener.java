package Display;

public interface ConnectListener {
	public void needConnect(String login, char[] passwd, String host, int port);
}
