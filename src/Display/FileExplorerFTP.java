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
