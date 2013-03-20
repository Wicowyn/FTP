/*//////////////////////////////////////////////////////////////////////
	This file is part of FTP, an client FTP.
	Copyright (C) 2013  Nicolas Barranger <wicowyn@gmail.com>

    FTP is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FTP is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FTP.  If not, see <http://www.gnu.org/licenses/>.
*///////////////////////////////////////////////////////////////////////

package Display;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
	private JFormattedTextField port=new JFormattedTextField(DecimalFormat.getIntegerInstance());
	private JButton button=new JButton("Connetion");
	private boolean enabled=true;
	
	public ConnectPan(){
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(new JLabel("Login: "));
		add(this.login);
		add(new JLabel("Mot de passe: "));
		add(this.passwd);
		add(new JLabel("Host: "));
		add(this.host);
		add(new JLabel("Port: "));
		add(this.port);
		
		ListenText lst=new ListenText();
		this.login.addKeyListener(lst);
		this.passwd.addKeyListener(lst);
		this.host.addKeyListener(lst);
		this.port.addKeyListener(lst);
		
		this.port.setValue(new Long(21));
		
		this.button=new JButton("Connection");
		button.setEnabled(false);
		this.button.addMouseListener(new ConnListener());
		add(this.button);
	
	}
	
	public void setEnabled(boolean enable){
		this.login.setEnabled(enable);
		this.passwd.setEnabled(enable);
		this.host.setEnabled(enable);
		this.port.setEnabled(enable);
		
		this.button.setText(enable ? "Connection" : "Deconnection");
		
		this.enabled=enable;
	}
	
	public boolean isEnable(){
		return this.enabled;
	}
	
	public void addListener(ConnectListener listener){
		this.listeners.add(listener);
	}
	
	public boolean removeListener(ConnectListener listener){
		return this.listeners.remove(listener);
	}
	
	protected void notifyConnect(String login, char[] passwd, String host, long port){
		for(ConnectListener listener : this.listeners) listener.needConnect(login, passwd, host, port);
	}
	
	protected void notifyDisconnect(){
		for(ConnectListener listener : this.listeners) listener.needDisconnect();
	}
	
	private class ListenText implements KeyListener{

		@Override
		public void keyPressed(KeyEvent e) {
			//Todo nothing
			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			ConnectPan th=ConnectPan.this;
			if(th.isEnable()) th.button.setEnabled(!th.login.getText().isEmpty() 
					&& th.passwd.getPassword().length!=0
					&& !th.host.getText().isEmpty()
					&& th.port.getValue()!=null);
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			//Todo nothing
		}
		
	}
	
	private class ConnListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			if(ConnectPan.this.isEnable()){
				ConnectPan.this.notifyConnect(
						ConnectPan.this.login.getText(),
						ConnectPan.this.passwd.getPassword(),
						ConnectPan.this.host.getText(),
						(Long) ConnectPan.this.port.getValue());
			}
			else ConnectPan.this.notifyDisconnect();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			//Todo nothing
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			//Todo nothing
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			//Todo nothing
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			//Todo nothing
			
		}
		
	}
}
