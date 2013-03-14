/*//////////////////////////////////////////////////////////////////////
	This file is part of FTP, an client FTP.
	Copyright (C) 2013  Nicolas Barranger <wicowyn@gmail.com>

    FTP is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FTP is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FTP.  If not, see <http://www.gnu.org/licenses/>.
*///////////////////////////////////////////////////////////////////////

package FTP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class PiFTP{
	private List<PiFTPListener> listeners=new ArrayList<PiFTPListener>();
	private BufferedReader in;
	private BufferedWriter out;
	private boolean connected=false;
	private Type type;
	
	
	public PiFTP(InputStream in, OutputStream out) throws IOException{
		setInputream(in);
		setOutputStream(out);		
	}
	
	public boolean connect(String id, String passwd){
		try {
			if(!command("USER "+id).startsWith("331 ")) return false;
			if(!command("PASS "+passwd).startsWith("230 ")) return false;
			
			if(isConnected()){
				this.connected=false;
				notifyDisconnected();
			}
			
			this.connected=true;
			notifyConnected();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public synchronized List<FTPFile> getFiles(String path){
		List<FTPFile> list=new ArrayList<FTPFile>();
		List<String> listName=new ArrayList<String>(), listInfo=new ArrayList<String>();
		Socket sock=null;
		BufferedReader read=null;
		
		if(this.type!=Type.A) if(!setMode(Type.A)) return list;
		try {
			sock=PASV();
			if(sock==null) return list;
			read = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			if(!command("NLST "+new String(path.getBytes(), "UTF-8")).startsWith("150")) return list;
			String str=read.readLine();
			while(str!=null){
				listName.add(str);
				str=read.readLine();
			}
			
			read.close();
			sock.close();
			
			//while(!this.in.ready()) try { Thread.sleep(150); } catch (InterruptedException e1) {}
			str=this.in.readLine();
			notifyReceiveMsg(str);
			if(!str.startsWith("226")) return list;
			
			sock=PASV();
			if(sock==null) return list;
			read = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			if(!command("LIST "+new String(path.getBytes(), "UTF-8")).startsWith("150")) return list;
			
			str=read.readLine();
			while(str!=null){
				listInfo.add(str);
				str=read.readLine();
			}
			
			read.close();
			sock.close();
			
			//while(!this.in.ready()) try { Thread.sleep(150); } catch (InterruptedException e1) {}
			str=this.in.readLine();
			notifyReceiveMsg(str);
			if(!str.startsWith("226")) return list;
			
			//after all... we can beginning
			if(listName.size()!=listInfo.size()) return list; //or not?
			SimpleDateFormat parseDTF=new SimpleDateFormat("yyyy MMM dd HH:mm", Locale.ENGLISH);
			for(int i=0; i<listName.size(); i++){
				FTPFile file=new FTPFile();
				String[] info=listInfo.get(i).split(" +"); //regex: un espace ou plus
				
				file.exist=true;
				file.duty=info[0];
				file.owner=info[2];
				file.ownerGroup=info[3];
				file.size=Long.parseLong(info[4]);
				
				try {
					file.date=parseDTF.parse("2013 "+info[5]+" "+info[6]+" "+info[7]);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				String abs=listName.get(i);
				int lastSlash=abs.lastIndexOf('/');
				file.path= lastSlash<1 ? "" : abs.substring(0, lastSlash-1);
				file.name=abs.substring(abs.lastIndexOf('/')+1, abs.length());				
				list.add(file);
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally{
			try {
				if(read!=null) read.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				if(sock!=null) sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	public boolean rename(FTPFile file, String newName){
		return move(file, file.getAbsPath()+"/"+newName);
	}
	
	public boolean move(FTPFile file, String newAbsPath){
		try {
			if(!command("RNFR "+new String(file.getAbsPath().getBytes(), "UTF-8")).startsWith("350")) return false;
			if(!command("RNTO "+new String(newAbsPath.getBytes(), "UTF-8")).startsWith("250")) return false;
			

			file.path=newAbsPath.substring(0, newAbsPath.lastIndexOf('/')-1);
			file.name=newAbsPath.substring(newAbsPath.lastIndexOf('/')+1, newAbsPath.length());		
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public InputStream download(FTPFile file){
		InputStream in=null;
		if(this.type!=Type.I) if(!setMode(Type.I)) return in;
		Socket sock=PASV();
		if(sock==null) return null;
		
		try {
			String log=command("RETR "+new String(file.getAbsPath().getBytes(), "UTF-8"));
			
			if(log.startsWith("125") || log.startsWith("150") || log.startsWith("350"))
					in=sock.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return in;
	}
	
	public OutputStream upload(String absPath){
		OutputStream out=null;
		if(this.type!=Type.I) if(!setMode(Type.I)) return out;
		Sock sock=PASV();
		if(sock==null) return null;
		
		try {
			String log=command("STOR "+new String(absPath.getBytes(), "UTF-8"));
			
			if(log.startsWith("125") || log.startsWith("150"))
					out=sock.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return out;
	}

	public boolean delete(FTPFile file){
		try {
			if(!command("DELE "+new String(file.getAbsPath().getBytes(), "UTF-8")).startsWith("250")) return false;
			file.exist=false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;		
		}
		return true;
	}

	protected Sock PASV(){
		Sock sock=null;	
		String log;
		
		try {
			log=command("PASV");
			
			if(log.startsWith("227")){
				String[] tab=log.substring(log.indexOf("(")+1, log.indexOf(")")).split(",");
				String host=tab[0]+"."+tab[1]+"."+tab[2]+"."+tab[3];
				int port=(Integer.parseInt(tab[4])<<8)+Integer.parseInt(tab[5]);
				
				sock=new Sock(host, port);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(sock==null);
		return sock;		
	}
	
	
	public boolean isConnected(){
		return this.connected;
	}
	
	protected synchronized String command(String cmd) throws IOException{
		while(this.in.ready()) notifyReceiveMsg(this.in.readLine()); //secure clearing
		this.out.write(cmd+"\r\n");
		this.out.flush();
		notifySendMsg(cmd);

		String str;
		try {
			str=this.in.readLine();
			
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
		while(this.in.ready()) notifyReceiveMsg(this.in.readLine()); //secure clearing
		
		return str==null ? new String() : str;
	}
	
	protected boolean setMode(Type type){
		try {
			if(command("TYPE "+type).startsWith("200")){
				this.type=type;
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return false;		
	}
	
	public Type getType(){
		return this.type;
	}
	
	protected void setInputream(InputStream in){
		try {
			if(this.in!=null) this.in.close();
			this.in=new BufferedReader(new InputStreamReader(in, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			System.err.println("WTF?");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void setOutputStream(OutputStream out){
		try {
			if(this.out!=null) this.out.close();
			this.out=new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
		} catch (UnsupportedEncodingException e) { 
			System.err.println("WTF?");
		} catch (IOException e) {
			e.printStackTrace();
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
	
	protected void notifySendMsg(String msg){
		for(PiFTPListener listener : this.listeners) listener.sendMsg(msg);
	}
	
	protected void notifyConnected(){
		for(PiFTPListener listener : this.listeners) listener.connected();
	}
	
	protected void notifyDisconnected(){
		for(PiFTPListener listener : this.listeners) listener.disconnected();
	}
	
	private enum Type{
		A, E, I, L
	}
	
}
