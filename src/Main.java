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
		//Socket sockPi=new Socket("88.162.36.15", 21126);
		Socket sockDtp=new Socket("localhost", 39775);
		/*
		PiFTP pi=new PiFTP(sockPi.getInputStream(), sockPi.getOutputStream());
		Thread threadPi=new Thread(pi);
		
		pi.addLisener(new PiFTPListener() {
			@Override
			public void receiveMsg(String msg) {
				System.out.println(msg);
				
			}
		});
		*/
		DtpFTP dtp=new DtpFTP(sockDtp.getInputStream(), sockDtp.getOutputStream());
		Thread threadDt=new Thread(dtp);
		
		//threadPi.start();
		threadDt.start();
		//pi.connect("yapiti");
	}
}
