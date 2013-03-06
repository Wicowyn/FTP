import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class PiFTP{
	private List<PiFTPListener> listeners=new ArrayList<PiFTPListener>();
	private BufferedReader in;
	private BufferedWriter out;
	private boolean connected=false;
	
	
	public PiFTP(InputStream in, OutputStream out){
		setInputream(in);
		setOutputStream(out);		
	}
	
	public boolean connect(String id, String passwd){
		try {
			if(!command("USER "+id).startsWith("331 ")) return false;
			if(!command("PASS "+passwd).startsWith("230 ")) return false;
			
			this.connected=true;
			notifyConnected();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean isConnected(){
		return this.connected;
	}
	
	protected String command(String cmd) throws IOException{
		while(in.ready()) notifyReceiveMsg(in.readLine()); //secure clearing
		out.write(cmd+"\r\n");
		out.flush();

		String str;
		try {
			str=in.readLine();
			
			if(str.startsWith("530") && isConnected()){
				this.connected=false;
				notifyDisconnected();
			}
			
			notifyReceiveMsg(str);
			
		} catch (IOException e) {
			e.printStackTrace();
			
			if(isConnected()){
				this.connected=false;
				notifyDisconnected();
			}
			
			throw e;
		}
		
		return str==null ? new String() : str;
	}
	
	public void setInputream(InputStream in){
		try {
			this.in=new BufferedReader(new InputStreamReader(in, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			System.out.println("WTF?");
		}
	}
	
	public void setOutputStream(OutputStream out){
		try {
			this.out=new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
		} catch (UnsupportedEncodingException e) { 
			System.out.println("WTF?");
		}
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
	
	protected void notifyConnected(){
		for(PiFTPListener listener : this.listeners) listener.connected();
	}
	
	protected void notifyDisconnected(){
		for(PiFTPListener listener : this.listeners) listener.disconnected();
	}
}
