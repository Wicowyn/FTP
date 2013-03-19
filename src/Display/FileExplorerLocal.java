package Display;

import java.io.File;

public class FileExplorerLocal extends FileExplorer {
	private static final long serialVersionUID = 5791387224671240515L;
	
	
	public FileExplorerLocal(){
		super(System.getProperty("user.home"));
	}	
	
	public void setPath(String path){
		this.path=path;
		File fileRoot=new File(this.path+"/");
		this.model.clear();
		
		if(!this.path.isEmpty()) this.model.addElement("..");
		for(File file : fileRoot.listFiles()){
			this.model.addElement(file.getName());
		}
	}
	
	
}
