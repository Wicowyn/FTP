
public interface PiFTPListener {
	void receiveMsg(String msg);
	void connected();
	void disconnected();
}
