package Display;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import FTP.FTPFile;
import FTP.PiFTP;
import FTP.PiFTPListener;
import FTP.TransferTask;
import FTP.TransferTaskListener;

public class Display extends JFrame {
	private static final long serialVersionUID = -17473638218276059L;
	private PiFTP pi;
	private Socket sock;
	private ConnectPan conn=new ConnectPan();
	private FileExplorerLocal expLocal=new FileExplorerLocal();
	private FileExplorerFTP expFTP=new FileExplorerFTP();
	private JProgressBar progressBar=new JProgressBar();
	
	public Display(){
		JPanel pan=new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
		setContentPane(pan);
		
		this.conn.setPreferredSize(new Dimension(600, 25));
		pan.add(this.conn);		
		
		JPanel panTree=new JPanel();
		panTree.setLayout(new BoxLayout(panTree, BoxLayout.X_AXIS));
		panTree.add(this.expLocal);
		panTree.add(this.expFTP);
		pan.add(panTree);	
		
		pan.add(this.progressBar);
		this.progressBar.setVisible(false);
		
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
						Display.this.pi.upload(Display.this.expFTP.getCurrentPath()+"/"+file.getName()));
				
				ListenTransfert lt=new ListenTransfert();
				lt.setSize((int) file.length());
				trf.addListener(lt);
				
				Thread th=new Thread(trf);
				th.start();
				Display.this.progressBar.setVisible(true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class ListenExpFTP implements FileExplorerListener{
		
		@Override
		public void selectedFile(String path) {
			if(Display.this.pi==null) return;
			FTPFile fileR=null;
			System.out.println(path);
			System.out.println(path.substring(0, path.lastIndexOf("/")));
			List<FTPFile> list=Display.this.pi.getFiles(path.substring(0, path.lastIndexOf("/")));
			for(FTPFile fl : list){
				if(fl.getAbsPath().equals(path)){
					System.out.println(path+" - "+fl.getAbsPath());
					fileR=fl;
				}
			}
			
			File fileL=new File(Display.this.expLocal.getCurrentPath()+"/"+fileR.getName());
			try {
				TransferTask trf=new TransferTask(
						Display.this.pi.download(fileR),
						new FileOutputStream(fileL));
				
				ListenTransfert lt=new ListenTransfert();
				lt.setSize((int) fileR.size());
				trf.addListener(lt);
				
				Thread th=new Thread(trf);
				th.start();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class ListenTransfert implements TransferTaskListener{		
		
		public void setSize(int size){
			Display.this.progressBar.setMaximum(size);
		}
		
		@Override
		public void transfered(long transfered) {
			Display.this.progressBar.setValue((int) transfered);
		}

		@Override
		public void finish() {
			Display.this.progressBar.setVisible(false);
		}
		
	}
	
	private class ListenConnect implements ConnectListener{

		@Override
		public void needConnect(String login, char[] passwd, String host, long port) {
			try {
				Socket sock=new Socket(host, (int) port);
				
				PiFTP pi=new PiFTP(sock.getInputStream(), sock.getOutputStream());
				pi.addLisener(new PiFTPListener() {
					
					@Override
					public void sendMsg(String msg) {
						System.out.println("send: "+msg);
						
					}
					
					@Override
					public void receiveMsg(String msg) {
						System.out.println("recv: "+msg);
						
					}
					
					@Override
					public void disconnected() {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void connected() {
						// TODO Auto-generated method stub
						
					}
				});
				if(pi.connect(login, new String(passwd))){
					Display.this.conn.setEnabled(false);
					Display.this.pi=pi;
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
			Display.this.pi=null;
			Display.this.expFTP.setPiFTP(null);
			
			Display.this.sock=null;
		}
		
	}
}
