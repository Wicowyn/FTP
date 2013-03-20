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
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Class that allow to dialog with an remote FTP server trough InputStream/OutputStream
 * @author yapiti
 *
 */
public class PiFTP{
	private List<PiFTPListener> listeners=new ArrayList<PiFTPListener>();
	private BufferedReader in;
	private BufferedWriter out;
	private boolean connected=false;
	private Type type;
	
	
	public PiFTP(InputStream in, OutputStream out) throws IOException{
		setInputStream(in);
		setOutputStream(out);		
	}
	
	/**
	 * Try to logging
	 * @param id login of user
	 * @param passwd password of user
	 * @return success or not
	 */
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
	
	/**
	 * Get remote file in the given path
	 * @param path The directory
	 * @return Files
	 */
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

			for(String infos : listInfo){
				String[] info=infos.split(" +"); //regex: un espace ou plus
				FTPFile file=new FTPFile();
				
				file.exist=true;
				file.duty=info[0];
				file.owner=info[2];
				file.ownerGroup=info[3];
				file.size=Long.parseLong(info[4]);
				
				try {
					file.date=parseDTF.parse("2013 "+info[5]+" "+info[6]+" "+info[7]);
				} catch (ParseException e) {
					e.printStackTrace();
					file.date=new Date();
				}				
				
				for(String abs : listName){
					int lastSlash=abs.lastIndexOf('/');
					String pName=abs.substring(lastSlash+1, abs.length());
					
					if(infos.contains(pName)){
						file.path= lastSlash<1 ? new String() : abs.substring(0, lastSlash);
						file.name=pName;
						
						break;
					}
				}
				
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
	
	/**
	 * Give the file focused by the given path, if it's not found return null;
	 * @param path the absolute path
	 * @return the file or null
	 */
	public synchronized FTPFile getFile(String path){
		FTPFile fileR=null;
		int index=path.lastIndexOf("/");
		
		if(index!=-1){
			List<FTPFile> list=getFiles(path.substring(0, path.lastIndexOf("/")));
			for(FTPFile fl : list){
				System.out.println(fl.getAbsPath()+" - "+path);
				if(fl.getAbsPath().equals(path)) fileR=fl;
			}
		}
		
		return fileR;
	}
	
	/**
	 * Rename the file by the given name
	 * @param file the file
	 * @param newName his new name
	 * @return success or not
	 */
	public synchronized boolean rename(FTPFile file, String newName){
		return move(file, file.getAbsPath()+"/"+newName);
	}
	
	/**
	 * Move an file
	 * @param file the file
	 * @param newAbsPath his new absolute path
	 * @return sucess or not
	 */
	public synchronized boolean move(FTPFile file, String newAbsPath){
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
	
	/**
	 * Allow to download an remote file
	 * @param file the file
	 * @return can return null if the file is not found
	 */
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
	
	/**
	 * Allow to upload an file
	 * @param absPath the remote absolute path
	 * @return can return null in some case
	 */
	public OutputStream upload(String absPath){
		OutputStream out=null;
		if(this.type!=Type.I) if(!setMode(Type.I)) return out;
		Socket sock=PASV();
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

	/**
	 * Delete the file
	 * @param file the file
	 * @return success or not
	 */
	public boolean delete(FTPFile file){
		try {
			if(!command((file.isDirectory() ? "RMD " : "DELE ")+new String(file.getAbsPath().getBytes(), "UTF-8")).startsWith("250")) return false;
			file.exist=false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;		
		}
		return true;
	}

	/**
	 * Return an socket to transfer data in passive mode
	 * @return
	 */
	protected Socket PASV(){
		Socket sock=null;	
		String log;
		
		try {
			log=command("PASV");
			
			if(log.startsWith("227")){
				String[] tab=log.substring(log.indexOf("(")+1, log.indexOf(")")).split(",");
				String host=tab[0]+"."+tab[1]+"."+tab[2]+"."+tab[3];
				int port=(Integer.parseInt(tab[4])<<8)+Integer.parseInt(tab[5]);
				
				sock=new Socket(host, port);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sock;		
	}
	
	/**
	 * We are connected?
	 * @return yes or no
	 */
	public boolean isConnected(){
		return this.connected;
	}
	
	/**
	 * Send an command to the server FTP
	 * @param cmd the command
	 * @return the reply
	 * @throws IOException
	 */
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
	
	/**
	 * Set the mode to transfer data
	 * @param type The type
	 * @return success or not
	 */
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
	
	/**
	 * Current mode to transfer data
	 * @return the mode
	 */
	public Type getType(){
		return this.type;
	}
	
	/**
	 * Set the location where reply arrive (warning: you must clean the reply of welcome of FTP server)
	 * @param in The InputStream
	 */
	public void setInputStream(InputStream in){
		try {
			if(this.in!=null) this.in.close();
			this.in=new BufferedReader(new InputStreamReader(in, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			System.err.println("WTF?");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the location where we must send issue to the server FTP
	 * @param out
	 */
	public void setOutputStream(OutputStream out){
		try {
			if(this.out!=null) this.out.close();
			this.out=new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
		} catch (UnsupportedEncodingException e) { 
			System.err.println("WTF?");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add an listener
	 * @param listener the listener
	 */
	public void addLisener(PiFTPListener listener){
		this.listeners.add(listener);
	}
	
	/**
	 * Remove an listener
	 * @param listener the listener
	 * @return find and removed or not
	 */
	public boolean removeListener(PiFTPListener listener){
		return this.listeners.remove(listener);
	}
	
	/**
	 * Notify listeners
	 * @param msg
	 */
	protected void notifyReceiveMsg(String msg){
		for(PiFTPListener listener : this.listeners) listener.receiveMsg(msg);
	}
	
	/**
	 * Notify listeners
	 * @param msg
	 */
	protected void notifySendMsg(String msg){
		for(PiFTPListener listener : this.listeners) listener.sendMsg(msg);
	}
	
	/**
	 * Notify listeners
	 */
	protected void notifyConnected(){
		for(PiFTPListener listener : this.listeners) listener.connected();
	}
	
	/**
	 * Notify listeners
	 */
	protected void notifyDisconnected(){
		for(PiFTPListener listener : this.listeners) listener.disconnected();
	}
	
	public enum Type{
		A, E, I, L
	}
	
}
