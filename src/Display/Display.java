package Display;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class Display extends JFrame {
	private static final long serialVersionUID = -17473638218276059L;
	private ConnectPan conn=new ConnectPan();
	private DefaultMutableTreeNode rootLocal=new DefaultMutableTreeNode();
	private JTree treeLocal=new JTree(this.rootLocal);
	private DefaultMutableTreeNode rootFTP=new DefaultMutableTreeNode();
	private JTree treeFTP=new JTree(this.rootFTP);
	
	public Display(){
		JPanel pan=new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
		setContentPane(pan);
		
		//this.conn.setMaximumSize(new Dimension(getWidth(), 80));
		this.conn.setPreferredSize(new Dimension(600, 25));
		pan.add(this.conn);		
		
		JPanel panTree=new JPanel();
		panTree.setLayout(new BoxLayout(panTree, BoxLayout.X_AXIS));
		panTree.add(this.treeLocal);
		panTree.add(this.treeFTP);
		pan.add(panTree);	
		
		this.treeLocal.addTreeSelectionListener(new TreeLocalListener());
		this.treeFTP.addTreeSelectionListener(new TreeFTPListener());
		showLocal("/home/yapiti");
		
	}
	
	private void showLocal(String path){
		File fileRoot=new File(path);
		this.rootLocal.removeAllChildren();
		
		for(File file : fileRoot.listFiles()){
			DefaultMutableTreeNode node=new DefaultMutableTreeNode(file.getName());
			this.rootLocal.add(node);
		}
		
		
	}
	
	private class TreeLocalListener implements TreeSelectionListener{

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			Display.this.showLocal("");
		}
		
	}
	
	private class TreeFTPListener implements TreeSelectionListener{

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			
		}
		
	}
}
