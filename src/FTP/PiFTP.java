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
	
	protected String getVal(String line, String name){
		int indxF=line.indexOf(name);
		if(indxF==-1) return null;
		int indxL=line.indexOf(';', indxF);
		
		return line.substring(indxF+name.length()+1, indxL);
	}
	
	protected FTPFile parseLine(String line){
	SimpleDateFormat parseDTF=new SimpleDateFormat("yyyyMMddHHmm");
		FTPFile file=new FTPFile();
		
		try {
			file.date=parseDTF.parse(getVal(line, "modify"));
		} catch (ParseException e) {
			System.err.println(e.getMessage());
		}
		file.perm=getVal(line, "perm");
		file.type=getVal(line, "type");
		if(!file.isDirectory()) file.size=Long.parseLong(getVal(line, "size"));
		file.absPath=line.substring(line.lastIndexOf("; ")+2, line.length());
		if(line.contains("UNIX.owner")) file.owner=Integer.parseInt(getVal(line, "UNIX.owner"));
		if(line.contains("UNIX.group")) file.owner=Integer.parseInt(getVal(line, "UNIX.group"));
		if(line.contains("UNIX.mode")) file.owner=Integer.parseInt(getVal(line, "UNIX.mode"));
		
		return file;
	}
	
	/**
	 * Get remote file in the given path
	 * @param path The directory
	 * @return Files
	 */
	public synchronized List<FTPFile> getFiles(String path){
		List<FTPFile> list=new ArrayList<FTPFile>();
		
		if(this.type!=Type.A) setMode(Type.A);
		Socket sock=PASV();
		if(sock==null) return list;
		
		try {
			BufferedReader read = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			if(!command("MLSD "+path).startsWith("150")) return list;
			
			String str=read.readLine();
			while(str!=null){
				FTPFile file=parseLine(str);
				file.exist=true;
				
				list.add(file);
				str=read.readLine();
			}
			
			read.close();
			sock.close();
			
			str=this.in.readLine();
			notifyReceiveMsg(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * Give the file focused by the given path, if it's not found return null;
	 * @param path the absolute path
	 * @return the file or null
	 */
	public synchronized FTPFile getFile(String path){
		FTPFile file=null;
				
		try {
			if(command("MLST "+path).startsWith("250-")){
				String line=this.in.readLine();
				file=parseLine(line.substring(4, line.length()));
				file.exist=true;
				
				notifyReceiveMsg(line);
				notifyReceiveMsg(this.in.readLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return file;
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
			if(!command("RNFR "+file.getAbsPath()).startsWith("350")) return false;
			if(!command("RNTO "+newAbsPath).startsWith("250")) return false;
			

			file.absPath=newAbsPath;	
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
	public synchronized InputStream download(FTPFile file){
		InputStream in=null;
		if(this.type!=Type.I) if(!setMode(Type.I)) return in;
		Socket sock=PASV();
		if(sock==null) return null;
		
		try {
			String log=command("RETR "+file.getAbsPath());
			
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
	public synchronized OutputStream upload(String absPath){
		OutputStream out=null;
		if(this.type!=Type.I && !setMode(Type.I)) return out;
		Socket sock=PASV();
		if(sock==null) return null;
		
		try {
			String log=command("STOR "+absPath);
			
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
			if(file.isDirectory()){
				List<FTPFile> list=getFiles(file.getAbsPath());
				
				for(FTPFile oneFile : list)	if(!delete(oneFile)) return false;
				
				if(!command("RMD "+file.getAbsPath()).startsWith("250")) return false;
			}
			else{
				if(!command("DELE "+file.getAbsPath()).startsWith("250")) return false;
			}
			
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
	 * We are connected? May be...
	 * @return yes or no
	 */
	public boolean isConnected(){
		return this.connected;
	}
	
	/**
	 * Just set attr connected to false
	 */
	public void disconnect(){
		this.connected=false;
	}
	
	/**
	 * Send an command to the server FTP
	 * @param cmd the command
	 * @return the reply
	 * @throws IOException
	 */
	protected synchronized String command(String cmd) throws IOException{
		while(this.in.ready()) notifyReceiveMsg(this.in.readLine()); //secure clearing
		this.out.write(new String(cmd.getBytes(), "UTF-8")+"\r\n");
		this.out.flush();
		notifySendMsg(cmd);

		String str;
		try {
			str=this.in.readLine();
			if((str==null && isConnected()) || (str.startsWith("530") && isConnected())){
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
		//while(this.in.ready()) notifyReceiveMsg(this.in.readLine()); //secure clearing
		
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
