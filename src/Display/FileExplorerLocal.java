package Display;

import java.io.File;

public class FileExplorerLocal extends FileExplorer {
	private static final long serialVersionUID = 5791387224671240515L;
	
	
	public void setPath(String path){
		this.path=path;
		File fileRoot=new File(this.path+"/");
		this.model.clear();
		
		if(!this.path.isEmpty()) this.model.addElement("..");
		for(File file : fileRoot.listFiles()){
			this.model.addElement(file.getName());
		}
	}


	@Override
	protected void selected(int index) {
		String path=this.path;
		String name=this.model.get(index);
		
		if(name.equals("..")){
			path=path.substring(0, path.lastIndexOf("/"));
			this.setPath(path);
		}
		else{
			path=path+"/"+this.model.get(index);
			File file=new File(path);
			if(file.isDirectory()){
				this.setPath(path);
			}
			else this.notifySelectedFile(path);
			
		}
		
	}
	
	
}
