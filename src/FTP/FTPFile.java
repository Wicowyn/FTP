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
