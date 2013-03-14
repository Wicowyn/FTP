package Display;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Display extends JFrame {
	private static final long serialVersionUID = -17473638218276059L;
	private ConnectPan conn=new ConnectPan();
	
	public Display(){
		JPanel pan=new JPanel();
		pan.setLayout(new FlowLayout(FlowLayout.LEFT,0,15));
		
		//this.conn.setMaximumSize(new Dimension(getWidth(), 80));
		this.conn.setPreferredSize(new Dimension(600, 25));
		pan.add(this.conn);
		setContentPane(pan);
	}
}
