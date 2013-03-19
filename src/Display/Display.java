package Display;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Display extends JFrame {
	private static final long serialVersionUID = -17473638218276059L;
	private ConnectPan conn=new ConnectPan();
	private FileExplorerLocal expLocal=new FileExplorerLocal();
	
	public Display(){
		JPanel pan=new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
		setContentPane(pan);
		
		this.conn.setPreferredSize(new Dimension(600, 25));
		pan.add(this.conn);		
		
		JPanel panTree=new JPanel();
		panTree.setLayout(new BoxLayout(panTree, BoxLayout.X_AXIS));
		panTree.add(this.expLocal);
		pan.add(panTree);	
		
		this.expLocal.addListener(new ListenExpLocal());
		
	}
	
	private class ListenExpLocal implements FileExplorerListener{
		@Override
		public void selectedFile(String path) {
			System.out.println(path);
			
		}
		
	}
	
	private class ListenExpFTP implements FileExplorerListener{
		@Override
		public void selectedFile(String path) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
