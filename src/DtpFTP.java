import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DtpFTP implements Runnable{
	private InputStream in;
	private OutputStream out;
	
	public DtpFTP(InputStream in, OutputStream out){
		this.in=in;
		this.out=out;
	}

	@Override
	public void run() {
		int i=0;
		while(i!=-1){
			try {
				i=in.read();
				System.out.print((char) i);
				//System.out.println(in.read());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
