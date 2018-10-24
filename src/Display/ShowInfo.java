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
	
	private JLabel name = new JLabel();
	private JLabel path = new JLabel();
	private JLabel size = new JLabel();
	private JLabel owner = new JLabel(); 
	private JLabel group = new JLabel(); 
	private JLabel type = new JLabel();
	private JLabel date = new JLabel();
	private JLabel perm = new JLabel();
	private Language lang;
	
	public ShowInfo(){
		lang = new Language();
		JPanel left=new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		
		left.add(new JLabel(lang.getPhrase(Language.INFO_DIR)));
		left.add(new JLabel(lang.getPhrase(Language.INFO_NAME)));
		left.add(new JLabel(lang.getPhrase(Language.INFO_TYPE)));
		left.add(new JLabel(lang.getPhrase(Language.INFO_SIZE)));
		left.add(new JLabel(lang.getPhrase(Language.INFO_DATE)));
		left.add(new JLabel(lang.getPhrase(Language.INFO_PERM)));
		left.add(new JLabel(lang.getPhrase(Language.INFO_OWNER)));
		left.add(new JLabel(lang.getPhrase(Language.INFO_GROUP)));
				
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
	
	@Override
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
