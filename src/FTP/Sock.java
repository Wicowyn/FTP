package FTP;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Sock extends Socket {

	public Sock(String string, int parseInt) throws UnknownHostException, IOException {
		super(string, parseInt);
	}

	@Override
	public void close() throws IOException{
		super.close();
		System.out.println("Sock close");
	}
}
