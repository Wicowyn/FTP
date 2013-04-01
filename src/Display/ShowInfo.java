package Display;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ShowInfo extends JDialog {
	private static final long serialVersionUID = -8863042376712432296L;
	private JLabel name=new JLabel(), path=new JLabel(), size=new JLabel(),
			owner=new JLabel(), group=new JLabel(), type=new JLabel(),
			date=new JLabel(), perm=new JLabel();
	
	public ShowInfo(){
		JPanel left=new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		
		left.add(new JLabel("Directory: "));
		left.add(new JLabel("Name: "));
		left.add(new JLabel("Type: "));
		left.add(new JLabel("Size: "));
		left.add(new JLabel("Date: "));
		left.add(new JLabel("Permissions: "));
		left.add(new JLabel("UNIX owner: "));
		left.add(new JLabel("UNIX group: "));
		
		JPanel right=new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		
		right.add(this.path);
		right.add(this.name);
		right.add(this.type);
		right.add(this.size);
		right.add(this.date);
		right.add(this.perm);
		right.add(this.owner);
		right.add(this.group);
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		add(left);
		add(right);
		
		setSize(300, 200);
	}
	
	public void setDir(String dir){
		this.path.setText(dir);
	}
	
	public void setName(String name){
		this.name.setText(name);
	}
	
	public void setTypeFile(String type){
		this.type.setText(type);
	}
	
	public void setSize(long size){
		this.size.setText(Long.toString(size));
	}
	
	public void setDate(Date date){
		DateFormat df=new SimpleDateFormat("dd/MM/yyyy HH:mm");
		this.date.setText(df.format(date));
	}
	
	public void setPerm(String perm){
		this.perm.setText(perm);
	}
	
	public void setUnixOwner(String owner){
		this.owner.setText(owner);
	}
	
	public void setUnixGroup(String group){
		this.group.setText(group);
	}
}
