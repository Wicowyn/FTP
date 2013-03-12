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
import java.util.Date;
import java.util.List;

import org.omg.CORBA.portable.OutputStream;



public class FTPFile {
	//private PiFTP ftp;
	protected String owner;
	protected String ownerGroup;
	protected long size;
	protected String duty; //linux-like
	protected String name;
	protected String path;
	protected Date date;
	
	
	protected FTPFile(){
		
	}
	
	public String getOwner(){
		return this.owner;
	}
	
	public String getOwnerGroup(){
		return this.ownerGroup;
	}
	
	public long size(){
		return this.size;
	}
	
	public boolean isDirectory(){
		return this.duty.startsWith("d");
	}
	
	public boolean isSimlik(){
		return this.duty.startsWith("l");
	}
	
	public String getPath(){
		return this.path;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getAbsPath(){
		return this.path+"/"+this.name;
	}
	
	public List<FTPFile> list(){
		return null;//isDirectory() ? FTPFile.getFiles(this.ftp, this.path+"/"+this.name) : null;
	}
	
	public Date getDate(){
		return this.date;
	}
	
	public OutputStream getData(){
		
		return null;
	}
}
