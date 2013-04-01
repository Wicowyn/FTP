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


public class FTPFile {
	//private PiFTP ftp;
	protected int owner;
	protected int ownerGroup;
	protected int mode;
	protected String perm;
	protected long size;
	protected String type; //linux-like
	protected String absPath;
	protected Date date;
	boolean exist=false;
	
	
	protected FTPFile(){
		
	}
	
	public int getUnixOwner(){
		return this.owner;
	}
	
	public int getUnixGroup(){
		return this.ownerGroup;
	}
	
	public String getType(){
		return this.type;
	}
	
	public int getMode(){
		return this.mode;
	}
	
	public String getPerm(){
		return this.perm;
	}
	
	public long size(){
		return this.size;
	}
	
	public boolean isDirectory(){
		return this.type.equals("dir") || this.type.equals("cdir") || this.type.equals("pdir");
	}
	
	public boolean exist(){
		return this.exist;
	}
	
	public boolean isSimlik(){
		return false;//this.duty.startsWith("l");
	}
	
	public String getPath(){
		int indx=this.absPath.lastIndexOf("/");
		return indx==-1 || indx==0 ? "/" : this.absPath.substring(0,  indx);
	}
	
	public String getName(){
		int indx=this.absPath.lastIndexOf("/");
		return indx==-1 ? new String(this.absPath) : this.absPath.substring(indx+1, this.absPath.length());
	}
	
	public String getAbsPath(){
		return this.absPath;
	}
	
	public Date getDate(){
		return this.date;
	}
}
