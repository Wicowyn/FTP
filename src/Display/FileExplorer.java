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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public abstract class FileExplorer extends JPanel{
	private static final long serialVersionUID = -5471662006897342640L;
	private List<FileExplorerListener> listeners=new ArrayList<FileExplorerListener>();
	protected DefaultListModel<String> model=new DefaultListModel<String>();
	protected JList<String> list=new JList<String>(this.model);
	protected String path=new String();
	protected JPopupMenu menu=new JPopupMenu();
	private int indxPopMenu=-1;
	
	public FileExplorer(){
		setLayout(new BorderLayout());
		this.list.addMouseListener(new ListenMouse());
		this.add(this.list);
		
		JMenuItem itemSuppr=new JMenuItem("Supprimer");
		itemSuppr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				delete(FileExplorer.this.indxPopMenu);
				
			}
		});
		this.menu.add(itemSuppr);
		
		JMenuItem itemInfo=new JMenuItem("Infos");
		itemInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				info(FileExplorer.this.indxPopMenu);
				
			}
		});
		this.menu.add(itemInfo);
		
		JMenuItem itemRename=new JMenuItem("Déplacer");
		itemRename.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String path=JOptionPane.showInputDialog("Le nouveau chemin",
						FileExplorer.this.path+"/"+FileExplorer.this.model.get(FileExplorer.this.indxPopMenu));
				if(path!=null) move(FileExplorer.this.indxPopMenu, path);
				
			}
		});
		this.menu.add(itemRename);
	}
	
	public String getCurrentPath(){
		return this.path;
	}
	
	public void clear(){
		this.model.clear();
	}
	
	public abstract void setPath(String path);
	
	protected abstract void selected(int index);
	
	protected abstract void move(int index, String newAbsPath);
	
	protected abstract void delete(int index);
	
	protected abstract void info(int index);
	
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
			if(e.getButton()==MouseEvent.BUTTON1){
				int index=FileExplorer.this.list.locationToIndex(e.getPoint());
				if(index!=this.lastIndex) this.nbClick=0;
				this.lastIndex=index;
				this.nbClick++;
				if(this.nbClick==2){
					this.nbClick=0;
					FileExplorer.this.selected(index);
				}
			}
			else{
				this.lastIndex=-1;
			}
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			//Todo nothing
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			//Todo nothing
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if(e.isPopupTrigger()){
				FileExplorer.this.indxPopMenu=FileExplorer.this.list.locationToIndex(e.getPoint());
				FileExplorer.this.menu.show(e.getComponent(), e.getX(), e.getY());
			}
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			//Todo nothing
			
		}
		
	}
}
