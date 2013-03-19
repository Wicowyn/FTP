package Display;

public interface ConnectListener {
	public void needConnect(String login, char[] passwd, String host, long port);
	public void needDisconnect();
}
