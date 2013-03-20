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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
	protected String path=new String();
	
	
	public FileExplorer(){
		this.list.addMouseListener(new ListenMouse());
		this.add(new JScrollPane(this.list));
	}
	
	public String getCurrentPath(){
		return this.path;
	}
	
	public abstract void setPath(String path);
	
	protected abstract void selected(int index);
	
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
				FileExplorer.this.selected(index);
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
