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

import java.io.File;
import java.util.Date;

public class FileExplorerLocal extends FileExplorer {
	private static final long serialVersionUID = 5791387224671240515L;
	
	
	public void setPath(String path){
		this.path=path;
		File fileRoot=new File(this.path + Constants.FILE_SEPARATOR);
		this.model.clear();
		
		if(!this.path.isEmpty()) this.model.addElement("..");
		for(File file : fileRoot.listFiles()){
			this.model.addElement(file.getName());
		}
		
		updateUI();
	}


	@Override
	protected void selected(int index) {
		String path=this.path;
		String name=this.model.get(index);
		
		if(name.equals("..")){
			path=path.substring(0, path.lastIndexOf(Constants.FILE_SEPARATOR));
			this.setPath(path);
		}
		else{
			path=path + Constants.FILE_SEPARATOR + this.model.get(index);
			File file=new File(path);
			
			if(file.isDirectory()){
				this.setPath(path);
			}
			else this.notifySelectedFile(path);
			
		}
		
	}


	@Override
	protected void delete(int index) {
		String name=this.model.get(index);
		
		if(!name.equals("..")){
			File file=new File(this.path + Constants.FILE_SEPARATOR + name);
			file.delete();
			setPath(this.path);
		}
		
	}


	@Override
	protected void move(int index, String newAbsPath) {
		String name=this.model.get(index);
		
		if(!name.equals("..")){
			File file=new File(this.path+"/"+name);
			file.renameTo(new File(newAbsPath));
			setPath(this.path);
		}
		
	}


	@Override
	protected void info(int index) {
		File file=new File(this.path + Constants.FILE_SEPARATOR + this.model.get(index));
		ShowInfo inf=new ShowInfo();
		
		inf.setDir(file.getPath());
		inf.setName(file.getName());
		inf.setTypeFile(file.isDirectory() ? "dir" : "file");
		inf.setSize(file.length());
		inf.setDate(new Date(file.lastModified()));
		/*inf.setPerm(file.getPerm());
		inf.setUnixOwner(Integer.toString(file.getUnixOwner()));
		inf.setUnixGroup(Integer.toString(file.getUnixGroup()));*/
		
		inf.setLocationRelativeTo(this);
		inf.setVisible(true);
	}
	
	
}
