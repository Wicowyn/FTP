import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class Main{

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		System.out.println("Hello world!");
		Socket sockPi=new Socket(args[0], Integer.parseInt(args[1]));
		
		PiFTP pi=new PiFTP(sockPi.getInputStream(), sockPi.getOutputStream());
		
		pi.addLisener(new PiFTPListener() {
			@Override
			public void receiveMsg(String msg) {
				System.out.println(msg);
				
			}

			@Override
			public void connected() {
				System.out.println("connected :)");
				
			}

			@Override
			public void disconnected() {
				System.out.println("diconnected :(");
				
			}
		});
		Thread.sleep(500);
		System.out.println(pi.connect(args[2], args[3]));
		
		sockPi.close();
	}
}
