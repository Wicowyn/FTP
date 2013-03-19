package Display;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public abstract class FileExplorer extends JPanel{
	private static final long serialVersionUID = -5471662006897342640L;
	private List<FileExplorerListener> listeners=new ArrayList<FileExplorerListener>();
	protected DefaultListModel<String> model=new DefaultListModel<String>();
	protected JList<String> list=new JList<String>(this.model);
	protected String path;
	
	
	public FileExplorer(String path){
		this.list.addMouseListener(new ListenMouse());
		this.add(new JScrollPane(this.list));
		setPath(path);
	}
	
	public abstract void setPath(String path);
	
	public void addListener(FileExplorerListener listener){
		this.listeners.add(listener);
	}
	
	public boolean removeListener(FileExplorerListener listener){
		return this.listeners.remove(listener);
	}
	
	protected void notifySelectedFile(String path){
		for(FileExplorerListener listener : this.listeners) listener.selectedFile(path);
	}
	
	private class ListenMouse implements MouseListener{
		private int lastIndex=-1;
		private int nbClick=-1;
		
		@Override
		public void mouseClicked(MouseEvent e) {
			int index=FileExplorer.this.list.locationToIndex(e.getPoint());
			if(index!=this.lastIndex) nbClick=0;
			this.lastIndex=index;
			this.nbClick++;
			if(this.nbClick==2){
				this.nbClick=0;
				String path=FileExplorer.this.path;
				String name=FileExplorer.this.model.get(index);
				
				if(name.equals("..")){
					path=path.substring(0, path.lastIndexOf("/"));
					FileExplorer.this.setPath(path);
				}
				else{
					path=path+"/"+FileExplorer.this.model.get(index);
					File file=new File(path);
					if(file.isDirectory()){
						FileExplorer.this.setPath(path);
					}
					
					FileExplorer.this.notifySelectedFile(path);
				}
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
