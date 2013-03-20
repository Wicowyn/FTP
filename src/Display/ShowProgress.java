package Display;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import FTP.TransferTask;
import FTP.TransferTaskListener;

public class ShowProgress extends JPanel {
	private static final long serialVersionUID = -5585045453142379373L;
	private DefaultListModel<JProgressBar> model=new DefaultListModel<JProgressBar>();
	private JList<JProgressBar> list=new JList<JProgressBar>(this.model);

	public ShowProgress(){
		add(new JScrollPane(this.list));
	}
	
	public void addTransferTask(TransferTask task){
		JProgressBar bar=new JProgressBar();
		bar.setMaximum((int) task.getSize());
		this.model.addElement(bar);
		
		ListenTask listener=new ListenTask();
		listener.bar=bar;
		task.addListener(listener);
		
		updateUI();
	}
	
	private class ListenTask implements TransferTaskListener{
		public JProgressBar bar;
		
		@Override
		public void transfered(long transfered) {
			this.bar.setValue((int) transfered);
			
		}

		@Override
		public void finish() {
			ShowProgress.this.model.removeElement(this.bar);
			this.bar=null;
			
		}
		
	}
}
