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

package Display;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import FTP.FTPFile;
import FTP.PiFTP;
import FTP.TransferTask;

public class Display extends JFrame {
	private static final long serialVersionUID = -17473638218276059L;
	private PiFTP pi=new PiFTP();
	private Socket sock;
	private ConnectPan conn=new ConnectPan();
	private LogFTP log=new LogFTP();
	private FileExplorerLocal expLocal=new FileExplorerLocal();
	private FileExplorerFTP expFTP=new FileExplorerFTP();
	private ShowProgress progress=new ShowProgress();
	
	public Display(){
		JPanel pan=new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
		setContentPane(pan);
		
		this.conn.setPreferredSize(new Dimension(800, 30));
		pan.add(this.conn);		
		
		this.pi.addLisener(this.log);
		this.log.setPreferredSize(new Dimension(800, 150));
		add(this.log);
		
		JPanel panTree=new JPanel();
		panTree.setLayout(new BoxLayout(panTree, BoxLayout.X_AXIS));
		JScrollPane scLocal=new JScrollPane(this.expLocal);
		JScrollPane scFTP=new JScrollPane(this.expFTP);
		scLocal.setPreferredSize(new Dimension(400, 560));
		scFTP.setPreferredSize(new Dimension(400, 560));
		panTree.add(scLocal);
		panTree.add(scFTP);
		pan.add(panTree);	
		
		JScrollPane scProgress=new JScrollPane(this.progress);
		scProgress.setPreferredSize(new Dimension(800, 100));
		pan.add(scProgress);
		
		this.conn.addListener(new ListenConnect());
		this.expLocal.addListener(new ListenExpLocal());
		this.expFTP.addListener(new ListenExpFTP());
		
		this.expLocal.setPath(System.getProperty("user.home"));
	}
	
	private class ListenExpLocal implements FileExplorerListener{
		
		@Override
		public void selectedFile(String path) {
			if(Display.this.pi==null) return;
			File file=new File(path);
			try {
				TransferTask trf=new TransferTask(
						new FileInputStream(file),
						Display.this.pi.upload(Display.this.expFTP.getCurrentPath()+"/"+file.getName()),
						file.length());
				
				Display.this.progress.addTransferTask(trf);
				
				Thread th=new Thread(trf);
				th.start();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class ListenExpFTP implements FileExplorerListener{
		
		@Override
		public void selectedFile(String path) {
			if(Display.this.pi==null) return;
			
			FTPFile fileR=Display.this.pi.getFile(path);
			if(fileR==null) return;
			File fileL=new File(Display.this.expLocal.getCurrentPath()+"/"+fileR.getName());
			
			try {
				TransferTask trf=new TransferTask(
						Display.this.pi.download(fileR),
						new FileOutputStream(fileL),
						fileR.size());
				
				Display.this.progress.addTransferTask(trf);
				
				Thread th=new Thread(trf);
				th.start();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class ListenConnect implements ConnectListener{

		@Override
		public void needConnect(String login, char[] passwd, String host, long port) {
			try {
				Socket sock=new Socket(host, (int) port);
				
				BufferedReader read=new BufferedReader(new InputStreamReader(sock.getInputStream()));
				read.readLine(); //To clear the reply of welcome..., but we should find another way.
				
				Display.this.pi.setInputStream(sock.getInputStream());
				Display.this.pi.setOutputStream(sock.getOutputStream());
				
				if(Display.this.pi.connect(login, new String(passwd))){
					Display.this.conn.setEnabled(false);
					Display.this.sock=sock;
					Display.this.expFTP.setPiFTP(pi);
					Display.this.expFTP.setPath("");
				}
				else{
					Display.this.conn.setEnabled(true);
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

		@Override
		public void needDisconnect() {
			Display.this.expFTP.setPiFTP(null);
			
			if(Display.this.sock!=null){
				try {
					Display.this.sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Display.this.sock=null;
			}
			
			Display.this.conn.setEnabled(true);
		}
		
	}
}
