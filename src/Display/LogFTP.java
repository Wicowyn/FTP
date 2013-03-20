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

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import FTP.PiFTPListener;

public class LogFTP extends JPanel implements PiFTPListener{
	private static final long serialVersionUID = 1719675270912008567L;
	private JTextArea text=new JTextArea();
	private JScrollPane scroll=new JScrollPane(this.text);

	public LogFTP() {
		setLayout(new BorderLayout());
		add(this.scroll);
	}
	
	@Override
	public void receiveMsg(String msg) {
		this.text.append("recv: "+msg+"\n");
		
	}

	@Override
	public void sendMsg(String msg) {
		this.text.append("send: "+msg+"\n");
		
	}

	@Override
	public void connected() {
		this.text.append("#### Connected!\n");
		
	}

	@Override
	public void disconnected() {
		this.text.append("#### Disconnected!\n");
		
	}

	
}
