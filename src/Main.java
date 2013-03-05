import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class Main{

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		System.out.println("Hello world!");
		Socket sock=new Socket("88.162.36.15", 21126);
		PiFTP pi=new PiFTP(sock.getInputStream(), sock.getOutputStream());
		Thread thread=new Thread(pi);
		
		pi.addLisener(new PiFTPListener() {
			@Override
			public void receiveMsg(String msg) {
				System.out.println(msg);
				
			}
		});
		
		thread.start();
		pi.connect("yapiti", "");
	}
}
