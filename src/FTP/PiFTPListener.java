package FTP;

public interface PiFTPListener {
	void receiveMsg(String msg);
	void sendMsg(String msg);
	void connected();
	void disconnected();
}
