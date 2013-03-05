import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class PiFTP implements Runnable{
	private List<PiFTPListener> listeners=new ArrayList<PiFTPListener>();
	private BufferedReader in;
	private PrintWriter out;
	private State state;
	
	
	public PiFTP(InputStream in, OutputStream out){
		this.in=new BufferedReader(new InputStreamReader(in));
		this.out=new PrintWriter(out, true);
	}
	
	public void connect(String id){
		this.state=State.Connect;
		this.out.print("USER "+id+"\r\n");
		this.out.flush();
	}
	
	public void setInputream(InputStream in){
		this.in=new BufferedReader(new InputStreamReader(in));
	}
	
	public void setOutputStream(OutputStream out){
		this.out=new PrintWriter(out, true);
	}
	
	@Override
	public void run() {
		try {
			String str=in.readLine();
			
			while(str!=null){
				notifyReceiveMsg(str);
				parse(str);
				str=in.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void parse(String cmd){
		
	}
	
	public void addLisener(PiFTPListener listener){
		this.listeners.add(listener);
	}
	
	public boolean removeListener(PiFTPListener listener){
		return this.listeners.remove(listener);
	}
	
	protected void notifyReceiveMsg(String msg){
		for(PiFTPListener listener : this.listeners) listener.receiveMsg(msg);
	}
	
	private enum State{
		None, Connect
	}
}
