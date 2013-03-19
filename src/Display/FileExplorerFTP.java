package Display;

import java.util.ArrayList;
import java.util.List;

import FTP.FTPFile;
import FTP.PiFTP;

public class FileExplorerFTP extends FileExplorer {
	private static final long serialVersionUID = 5350246563844990461L;
	private PiFTP pi=null;

	public FileExplorerFTP(String path, PiFTP pi) {
		super(path);
		this.pi=pi;
	}

	@Override
	public void setPath(String path) {
		List<FTPFile> list=new ArrayList<FTPFile>();
		
	}

}
