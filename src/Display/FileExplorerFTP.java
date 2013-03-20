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

import java.util.ArrayList;
import java.util.List;

import FTP.FTPFile;
import FTP.PiFTP;

public class FileExplorerFTP extends FileExplorer {
	private static final long serialVersionUID = 5350246563844990461L;
	private List<FTPFile> files=new ArrayList<FTPFile>();
	private PiFTP pi=null;

	public FileExplorerFTP() {
	
	}

	@Override
	public void setPath(String path) {
		this.path=path;
		this.model.clear();
		this.files.clear();
		if(this.pi==null) return;
		
		this.files=this.pi.getFiles(path);
		if(!this.path.isEmpty()) this.model.addElement("..");
		for(FTPFile file : this.files){
			this.model.addElement(file.getName());
		}
	}
	
	public void setPiFTP(PiFTP pi){
		this.pi=pi;
		this.model.clear();
		this.files.clear();
	}
	
	public PiFTP getPiFTP(){
		return this.pi;
	}

	@Override
	protected void selected(int index) {
		if(this.model.getElementAt(index).equals("..")){
			this.path=this.path.substring(0, this.path.lastIndexOf("/"));
			this.setPath(path);
		}
		else{
			FTPFile file=this.files.get(index);
			if(file.isDirectory()){
				setPath(file.getAbsPath());
			}
			else notifySelectedFile(file.getAbsPath());

		}
		
	}

}
