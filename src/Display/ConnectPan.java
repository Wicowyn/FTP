package Display;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ConnectPan extends JPanel{
	private static final long serialVersionUID = 4707425225997730750L;
	private List<ConnectListener> listeners=new ArrayList<ConnectListener>();
	private JTextField login=new JTextField();
	private JPasswordField passwd=new JPasswordField();
	private JFormattedTextField host=new JFormattedTextField();
	private JFormattedTextField port=new JFormattedTextField(new DecimalFormat("Integer"));
	
	public ConnectPan(){
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		//this.login.setSize(150, this.login.getHeight());
		add(new JLabel("Login: "));
		add(this.login);
		add(new JLabel("Mot de passe: "));
		add(this.passwd);
		add(new JLabel("Host: "));
		add(this.host);
		add(new JLabel("Port: "));
		add(this.port);
		
		JButton button=new JButton("Connection");
		button.addMouseListener(new ConnListener());
		add(button);
	
	}
	
	public void addListener(ConnectListener listener){
		this.listeners.add(listener);
	}
	
	public boolean removeListener(ConnectListener listener){
		return this.listeners.remove(listener);
	}
	
	protected void notifyConnect(String login, char[] passwd, String host, int port){
		for(ConnectListener listener : this.listeners) listener.needConnect(login, passwd, host, port);
	}
	
	private class ConnListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			ConnectPan.this.notifyConnect(
					ConnectPan.this.login.getText(),
					ConnectPan.this.passwd.getPassword(),
					ConnectPan.this.host.getText(),
					(int) ConnectPan.this.port.getValue());
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
		}
		
	}
}
