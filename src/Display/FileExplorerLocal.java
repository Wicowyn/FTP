package Display;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class FileExplorerLocal extends JPanel {
	private static final long serialVersionUID = 5791387224671240515L;
	private DefaultListModel<String> model=new DefaultListModel<String>();
	private JList<String> list=new JList<String>(this.model);
	private String path;
	
	
	public FileExplorerLocal(){
		this(System.getProperty("user.home"));
	}
	
	public FileExplorerLocal(String path){
		this.list.addMouseListener(new ListenMouse());
		this.add(new JScrollPane(this.list));
		setPath(path);
	}
	
	public void setPath(String path){
		this.path=path;
		
		File fileRoot=new File(this.path);
		this.model.clear();
		
		for(File file : fileRoot.listFiles()){
			this.model.addElement(file.getName());
		}
	}
	
	private class ListenMouse implements MouseListener{
		private int lastIndex=-1;
		private int nbClick=-1;
		
		@Override
		public void mouseClicked(MouseEvent e) {
			int index=FileExplorerLocal.this.list.locationToIndex(e.getPoint());
			if(index!=this.lastIndex) nbClick=0;
			this.lastIndex=index;
			this.nbClick++;
			if(this.nbClick==2){
				System.out.println("double clicked");
			}
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
